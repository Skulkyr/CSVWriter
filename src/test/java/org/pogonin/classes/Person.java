package org.pogonin.classes;
@SuppressWarnings("all")
public class Person {
    private String firstName;
    private String lastName;
    private Integer age;

    // Конструктор
    public Person(String firstName, String lastName, Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}
