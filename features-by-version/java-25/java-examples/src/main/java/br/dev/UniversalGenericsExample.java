package br.dev;

import br.dev.domain.User;
import br.dev.domain.Post;
import br.dev.domain.DataService;
import java.util.*;

public class UniversalGenericsExample {
    // Universal generics: works with primitives and reference types
    public static <T> void printList(List<T> list) {
        for (T item : list) {
            System.out.println(item);
        }
    }

    public static void main(String[] args) {
        // Example with primitives
        List<int> intList = List.of(1, 2, 3, 4, 5); // Java 25 syntax
        printList(intList);

        // Example with value types (hypothetical Point value class)
        List<Point> pointList = List.of(new Point(1, 2), new Point(3, 4));
        printList(pointList);

        // Example with domain objects
        DataService dataService = new DataService();
        List<User> users = dataService.getUserWithPost();
        printList(users);

        // Example with Optional of primitive
        Optional<double> average = Optional.of(3.1415); // Java 25 syntax
        average.ifPresent(val -> System.out.println("Average: " + val));
    }
}

// Hypothetical value class for demonstration
value class Point {
    private final int x;
    private final int y;
    public Point(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
    @Override public String toString() { return "Point[" + x + ", " + y + "]"; }
}

