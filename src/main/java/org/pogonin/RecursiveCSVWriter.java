package org.pogonin;

import org.pogonin.exceptions.DifferentClassesCollectionException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

/**
 * A specific implementation of the CSVWriter class, responsible for converting objects to CSV format.
 *
 * <p>Uses the settings defined in the CSVWriter base class and implements the methods.</p>
 *
 * @since 1.0
 */
public class RecursiveCSVWriter extends CSVWriter {

    /**
     * Constructor with settings by default
     */
    public RecursiveCSVWriter() {
    }


    @Override
    void writeToFile(Collection<?> objects, String filePath) throws IOException, IllegalAccessException {
        String csvData = getCSVString(objects);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(csvData);
        }
    }

    @Override
    public String writeToString(Collection<?> collection) throws IllegalAccessException {
        return getCSVString(collection);
    }


    /**
     * Converts a collection of objects into a CSV format string.
     *
     * @param collection a collection of objects to convert to CSV.
     * @return a CSV string representing the collection data.
     * @throws IllegalAccessException if object fields cannot be accessed.
     * @throws DifferentClassesCollectionException if the collection contains objects of different classes and
     * {@code ignoreOtherType} setting is set to {@code false}.
     */
    private String getCSVString(Collection<?> collection) throws IllegalAccessException {
        StringBuilder builder = new StringBuilder();
        Object[] objects = collection.toArray();
        Class<?> clazz = objects[0].getClass();
        Field[] fields = getFields(clazz);

        fillTitle(fields, builder, "", (byte) 0);
        builder.replace(builder.lastIndexOf(columnDelimiter), builder.length(), "\n");

        for (Object obj : objects) {
            if (!obj.getClass().equals(clazz)) {
                if (ignoreOtherType) continue;
                else throw new DifferentClassesCollectionException();
            }

            fillData(obj, fields, builder, (byte) 0);
            builder.replace(builder.lastIndexOf(columnDelimiter), builder.length(), "\n");
        }

        return builder.toString();
    }


    /**
     * Recursively populates CSV column headers using object field names.
     *
     * @param fields An array of class fields.
     * @param builder instance of {@code StringBuilder} to build the CSV string.
     * @param prefix Prefix for the field name (used for nested objects).
     * @param depth current recursion depth.
     */
    private void fillTitle(Field[] fields, StringBuilder builder, String prefix, Byte depth) {
        for (Field field : fields) {
            var fieldType = field.getType();
            field.setAccessible(true);
            String name = prefix + field.getName();

            if (isPrimitive(fieldType)) {
                builder.append(name);
                builder.append(columnDelimiter);

            } else if (fieldType.isArray() && allowArrays) {
                builder.append(name);
                builder.append(columnDelimiter);

            } else if (depth < maxDepth)
                fillTitle(
                        getFields(fieldType),
                        builder,
                        name + recursionDelimiter,
                        ++depth
                );
        }
    }


    /**
     * Recursively populates CSV data from an object using its field values.
     *
     * @param obj the object whose data should be written to CSV.
     * @param fields An array of object fields.
     * @param builder instance of {@code StringBuilder} to build the CSV string.
     * @param depth current recursion depth.
     * @throws IllegalAccessException if the object's fields cannot be accessed.
     */
    private void fillData(Object obj, Field[] fields, StringBuilder builder, Byte depth) throws IllegalAccessException {
        for (Field field : fields) {
            var fieldType = field.getType();
            field.setAccessible(true);
            var value = field.get(obj);

            if (value == null) {
                builder.append(columnDelimiter.repeat(
                        getCommasCountOfNullObject(fieldType, (byte) (depth + 1))));
            } else if (isPrimitive(fieldType)) {
                builder.append(value);
                builder.append(columnDelimiter);

            } else if (fieldType.isArray() && allowArrays) {
                Object[] array = (Object[]) value;
                for (Object o : array) {
                    builder.append(o.toString());
                    builder.append(arrayDelimiter);
                }
                builder.replace(builder.lastIndexOf(arrayDelimiter), builder.length(), columnDelimiter);

            } else if (depth < maxDepth)
                fillData(
                        value,
                        getFields(fieldType),
                        builder,
                        ++depth
                );
        }
    }


    /**
     * Gets an array of class fields depending on the {@code declared} setting.
     *
     * @param clazz the class whose fields are to be retrieved.
     * @return an array of class fields.
     */
    private Field[] getFields(Class<?> clazz) {
        return declared ? clazz.getDeclaredFields() : clazz.getFields();
    }


    /**
     * Calculates the number of delimiters for an object with the value {@code null} to preserve CSV format.
     *
     * @param clazz object class.
     * @param depth current recursion depth.
     * @return the number of delimiters needed to pad {@code null} values.
     */
    private int getCommasCountOfNullObject(Class<?> clazz, Byte depth) {
        if(isPrimitive(clazz) || clazz.isArray()) return 1;
        if(depth > maxDepth) return 0;

        int count = 0;
        for (Field field : getFields(clazz)) {
            Class<?> fieldType = field.getType();
            if (fieldType.isArray() || isPrimitive(fieldType))
                count++;
            else if (depth < maxDepth)
                count += getCommasCountOfNullObject(fieldType, ++depth);
        }
        return count;
    }


    /**
     * Checks whether the class is one of the primitive or simple data types.
     *
     * @param clazz class to check.
     * @return {@code true} if the class is a primitive type, string, number, boolean, or character.
     */
    private boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz.isEnum() ||
                clazz.equals(String.class) ||
                Number.class.isAssignableFrom(clazz) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Character.class);
    }
}
