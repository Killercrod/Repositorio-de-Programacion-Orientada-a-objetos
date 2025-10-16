package model;

import java.util.Objects;

public class Person {
    private final Long id;
    private final String name;
    private final int age;

    public Person(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age) {
        this(null, name, age);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age &&
                Objects.equals(id, person.id) &&
                Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }
}
