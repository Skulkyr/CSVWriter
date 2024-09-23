package org.pogonin.classes;
@SuppressWarnings("all")
public class Company {
    private String name;
    private String[] departments;

    // Конструктор
    public Company(String name, String[] departments) {
        this.name = name;
        this.departments = departments;
    }
}
