package org.pogonin;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pogonin.classes.Company;
import org.pogonin.classes.Employee;
import org.pogonin.classes.Person;
import org.pogonin.classes.Address;
import org.pogonin.exceptions.DifferentClassesCollectionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecursiveCSVWriterTest {

    private CSVWriter converter;

    @BeforeEach
    void init() {
        converter = new RecursiveCSVWriter();
    }

    @Test
    @DisplayName("Checking on 2 simple object")
    void testSimpleObjects() throws IllegalAccessException {
        List<Person> persons = Arrays.asList(
                new Person("John", "Doe", 30),
                new Person("Jane", "Smith", 25));
        String expectedCsv = """
                             firstName,lastName,age
                             John,Doe,30
                             Jane,Smith,25
                             """;


        String actual = converter.writeToString(persons);


        assertEquals(expectedCsv, actual);
    }

    @Test
    @DisplayName("Checking the conversion of nested objects")
    void testNestedObjects() throws IllegalAccessException {
        converter.setMaxDepth((byte) 2);
        List<Employee> employees = Arrays.asList(
                new Employee(new Person("John", "Doe", 30), "Engineering"),
                new Employee(new Person("Jane", "Smith", 25), "Marketing"));
        String expected = """
                          person.firstName,person.lastName,person.age,department
                          John,Doe,30,Engineering
                          Jane,Smith,25,Marketing
                          """;


        String actual = converter.writeToString(employees);


        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Checking array conversion")
    void testObjectsWithArrays() throws IllegalAccessException {
        converter.setAllowArrays(true);
        Company company = new Company("TechCorp", new String[]{"Engineering", "Marketing", "Sales"});
        String expected = """
                             name,departments
                             TechCorp,Engineering|Marketing|Sales
                             """;


        String actual = converter.writeToString(Collections.singletonList(company));


        assertEquals(expected, actual);
    }

    @Nested
    @DisplayName("Checking the conversion of collections with different classes")
    class testDifferentClasses {
        @Test
        @DisplayName("Exception check when ignore setting is disabled")
        void withIgnoreFlagFalse() {
            List<Object> mixedObjects = Arrays.asList(
                    new Person("John", "Doe", 30),
                    new Address("Main Street", "New York"));
            converter.setIgnoreOtherType(false);


            assertThrows(DifferentClassesCollectionException.class, () -> converter.writeToString(mixedObjects));
        }

        @Test
        @DisplayName("Checking for an exception when the ignore setting is enabled")
        void withIgnoreFlagTrue() throws IllegalAccessException {
            converter.setIgnoreOtherType(true);
            List<Object> mixedObjects = Arrays.asList(
                    new Person("John", "Doe", 30),
                    new Address("Main Street", "New York"));
            String expected = """
                    firstName,lastName,age
                    John,Doe,30
                    """;


            String actual = converter.writeToString(mixedObjects);


            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("Null handling check")
    class testNull {
        @Test
        @DisplayName("Checking a simple null object")
        void testNullValue () throws IllegalAccessException {
            List<Person> persons = Arrays.asList(
                    new Person(null, "Doe", 30),
                    new Person("Jane", null, null));
            String expected = """
                    firstName,lastName,age
                    ,Doe,30
                    Jane,,
                    """;


            String actual = converter.writeToString(persons);


            assertEquals(expected, actual);
        }


        @Test
        @DisplayName("Checking a complex null object")
        void testNullObject () throws IllegalAccessException {
            converter.setMaxDepth((byte) 2);
            List<Employee> employees = Arrays.asList(
                    new Employee(null, "department"),
                    new Employee(new Person("Ivan", "Petrov", 24), "department2"));
            String expected = """
                    person.firstName,person.lastName,person.age,department
                    ,,,department
                    Ivan,Petrov,24,department2
                    """;


            String actual = converter.writeToString(employees);


            assertEquals(expected, actual);
        }
    }

    @Test
    @DisplayName("Checking a file entry")
    void writeCsvFile() throws IOException, IllegalAccessException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        List<Person> persons = Arrays.asList(
                new Person("John", "Doe", 30),
                new Person("Jane", "Smith", 25)
        );


        converter.writeToFile(persons, tempFile.getPath());
        List<String> lines = Files.readAllLines(tempFile.toPath());


        assertEquals(3, lines.size());
        String header = lines.get(0);
        assertEquals("firstName,lastName,age", header);
        String firstDataLine = lines.get(1);
        assertEquals("John,Doe,30", firstDataLine);
        String secondDataLine = lines.get(2);
        assertEquals("Jane,Smith,25", secondDataLine);
    }
}

