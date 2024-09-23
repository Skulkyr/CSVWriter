package org.pogonin.classes;
@SuppressWarnings("all")
public class Employee {
    private Person person;
    private String department;

    // Конструктор
    public Employee(Person person, String department) {
        this.person = person;
        this.department = department;
    }
}
