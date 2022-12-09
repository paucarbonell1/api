package me.papaseca.objects;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Person {

    private String name;
    private int age;
    private String job;
    private String dni;

    public Person(String name, int age, String job, String dni) {
        this.name = name;
        this.age = age;
        this.job = job;
        this.dni =dni;
    }

}
