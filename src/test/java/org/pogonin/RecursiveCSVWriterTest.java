package org.pogonin;


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

    private final CSVWriter converter = new RecursiveCSVWriter();

    @Test
    void testSimpleObjects() throws IllegalAccessException {
        converter.setColumnDelimiter(",");
        converter.setRecursionDelimiter(".");
        converter.setMaxDepth((byte) 1);
        converter.setDeclared(true);
        converter.setIgnoreOtherType(false);
        converter.setAllowArrays(false);
        List<Person> persons = Arrays.asList(
                new Person("John", "Doe", 30),
                new Person("Jane", "Smith", 25)
        );


        String csv = converter.writeToString(persons);


        String expectedCsv = """
                firstName,lastName,age
                John,Doe,30
                Jane,Smith,25
                """;
        assertEquals(expectedCsv, csv);
    }

    @Test
    void testNestedObjects() throws IllegalAccessException {
        converter.setMaxDepth((byte) 2);
        List<Employee> employees = Arrays.asList(
                new Employee(new Person("John", "Doe", 30), "Engineering"),
                new Employee(new Person("Jane", "Smith", 25), "Marketing")
        );


        String csv = converter.writeToString(employees);


        String expectedCsv = """
                person.firstName,person.lastName,person.age,department
                John,Doe,30,Engineering
                Jane,Smith,25,Marketing
                """;
        assertEquals(expectedCsv, csv);
    }

    @Test
    void testObjectsWithArrays() throws IllegalAccessException {
        converter.setAllowArrays(true);
        Company company = new Company("TechCorp", new String[]{"Engineering", "Marketing", "Sales"});


        String csv = converter.writeToString(Collections.singletonList(company));


        String expectedCsv = """
                name,departments
                TechCorp,Engineering|Marketing|Sales
                """;
        assertEquals(expectedCsv, csv);
    }

    @Test
    void testNullValues() throws IllegalAccessException {
        List<Person> persons = Arrays.asList(
                new Person(null, "Doe", 30),
                new Person("Jane", null, null)
        );


        String csv = converter.writeToString(persons);


        String expectedCsv = """
                firstName,lastName,age
                ,Doe,30
                Jane,,
                """;
        assertEquals(expectedCsv, csv);
    }

    @Test
    void testDifferentClassesException() {
        List<Object> mixedObjects = Arrays.asList(
                new Person("John", "Doe", 30),
                new Address("Main Street", "New York")
        );


        converter.setIgnoreOtherType(false);


        assertThrows(DifferentClassesCollectionException.class, () -> converter.writeToString(mixedObjects));
    }

    @Test
    void testIgnoreDifferentClasses() throws IllegalAccessException {
        converter.setIgnoreOtherType(true);
        List<Object> mixedObjects = Arrays.asList(
                new Person("John", "Doe", 30),
                new Address("Main Street", "New York")
        );


        String csv = converter.writeToString(mixedObjects);


        String expectedCsv = """
                firstName,lastName,age
                John,Doe,30
                """;
        assertEquals(expectedCsv, csv);
    }

    @Test
    void testCountColumnDelimiters() throws IllegalAccessException {
        converter.setMaxDepth((byte) 2);
        List<Employee> employees = Arrays.asList(
                new Employee(null, "department"),
                new Employee(new Person("Ivan", "Petrov", 24), "department2")
        );


        String csv = converter.writeToString(employees);


        String expectedCsv = """
                person.firstName,person.lastName,person.age,department
                ,,,department
                Ivan,Petrov,24,department2
                """;
        assertEquals(expectedCsv, csv);
    }

    @Test
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

