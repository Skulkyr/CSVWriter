package org.pogonin;

import java.io.IOException;
import java.util.Collection;

/**
 * Abstract class for converting objects to CSV format.
 * Provides basic settings and methods for working with conversion.
 *
 * <p>Default settings:</p>
 * <ul>
 * <li><b>declared</b> (boolean): {@code true} - use all fields, including private and protected ones.</li>
 * <li><b>maxDepth</b> (Byte): {@code 0} - maximum recursion depth when processing nested objects.</li>
 * <li><b>allowArrays</b> (boolean): {@code false} - allow or disable processing of arrays.</li>
 * <li><b>arrayDelimiter</b> (String): {@code "|"} - delimiter for array elements in CSV.</li>
 * <li><b>columnDelimiter</b> (String): {@code ","} - column delimiter in CSV.</li>
 * <li><b>ignoreOtherType</b> (boolean): {@code true} - ignore objects of other types when writing to CSV.</li>
 * <li><b>recursionDelimiter</b> (String): {@code "."} - delimiter when recursively processing nested fields.</li>
 * </ul>
 *
 * <p>The class contains setters for all settings, which allows you to flexibly customize the conversion behavior.</p>
 *
 * <p>Also provides abstract methods for converting objects to a CSV string and writing to a file.</p>
 *
 * @since 1.0
 */

public abstract class CSVWriter {

   protected boolean declared = true;
   protected Byte maxDepth = 0;
   protected boolean allowArrays = false;
   protected String arrayDelimiter = "|";
   protected String columnDelimiter = ",";
   protected boolean ignoreOtherType = true;
   protected String recursionDelimiter = ".";

   /**
    * Writes a collection of objects to a CSV file.
    *
    * @param objects collection of objects to write
    * @param filePath path to the file to write
    * @throws IOException if an I/O error occurred
    * @throws IllegalAccessException if object fields cannot be accessed
    */
   abstract void writeToFile(Collection<?> objects, String filePath) throws IOException, IllegalAccessException;


   /**
    * Abstract method for converting a collection of objects to CSV format.
    *
    * @param collection collection of objects to convert
    * @return a string in CSV format
    * @throws IllegalAccessException if object fields cannot be accessed
    */
   abstract String writeToString(Collection<?> collection) throws IllegalAccessException;

   public void setDeclared(boolean declared) {
      this.declared = declared;
   }

   public void setMaxDepth(Byte maxDepth) {
      this.maxDepth = maxDepth;
   }

   public void setAllowArrays(boolean allowArrays) {
      this.allowArrays = allowArrays;
   }

   public void setArrayDelimiter(String arrayDelimiter) {
      this.arrayDelimiter = arrayDelimiter;
   }

   public void setColumnDelimiter(String columnDelimiter) {
      this.columnDelimiter = columnDelimiter;
   }

   public void setIgnoreOtherType(boolean ignoreOtherType) {
      this.ignoreOtherType = ignoreOtherType;
   }

   public void setRecursionDelimiter(String recursionDelimiter) {
      this.recursionDelimiter = recursionDelimiter;
   }
}
