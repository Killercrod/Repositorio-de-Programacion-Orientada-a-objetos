package view;

import model.Person;

import java.util.List;

public class PersonView {
    public void showCreated(Person p) {
        System.out.println("Created: " + p);
    }

    public void showList(List<Person> people) {
        System.out.println("People (" + people.size() + "):");
        for (Person p : people) {
            System.out.println(" - " + p);
        }
    }
}
