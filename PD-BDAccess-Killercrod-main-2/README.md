[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/FJrpkwzh)
[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=21118341&assignment_repo_type=AssignmentRepo)
# BDConnection
Completar las clases que sean necesarias para poder ejecutar el proyecto en local.


# BDInicialTest

A minimal Java 17 project using Maven and JUnit 5.

## Requirements
- JDK 17+
- Maven 3.8+

## Quick start

Build and run tests:

```sh
mvn -q -v
mvn -q clean test
```

Run the app:

```sh
mvn -q -DskipTests exec:java
```

Or run the compiled jar:

```sh
mvn -q -DskipTests package
java -jar target/bdinicialtest-0.0.1-SNAPSHOT.jar
```

## Project layout
- `src/main/java/com/example/App.java` — main application entry point
- `src/test/java/com/example/AppTest.java` — sample JUnit test
- `pom.xml` — Maven configuration
