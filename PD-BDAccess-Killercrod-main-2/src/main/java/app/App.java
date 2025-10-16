package app;

import bd.PersonRepository;
import controller.PersonController;
import view.PersonView;

public class App {
    public static void main(String[] args) {
    

        PersonRepository repo = new PersonRepository();
        PersonView view = new PersonView();
        PersonController controller = new PersonController(repo, view);
        repo.initBDPerson();
        // Seed a couple of demo persons
        controller.addPerson("Alice", 30);
        controller.addPerson("Bob", 25);
        controller.listPeople();
    }

    // kept for unit test example
    public static int add(int a, int b) {
        return a + b;
    }
}
