package br.dev;

// Example of JEP: Data Classes in Java 25
// Data classes provide concise syntax for immutable, data-centric classes
// This is a hypothetical syntax for demonstration purposes

data class User(String name, int age) {
    // Additional methods or logic can be added if needed
}

public class DataClassesExample {
    public static void main(String[] args) {
        User user = new User("Alice", 30);
        System.out.println(user); // User[name=Alice, age=30]
        System.out.println("Name: " + user.name());
        System.out.println("Age: " + user.age());
    }
}

