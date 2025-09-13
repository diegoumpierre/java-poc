package br.dev;

import br.dev.domain.Post;
import br.dev.domain.User;

public class PatternMatchingInstanceofExample {
    public static void main(String[] args) {
        Object obj1 = new Post("Java 17", "Pattern Matching Example", true);
        Object obj2 = new User("Alice", "alice@example.com");

        printObjectInfo(obj1);
        printObjectInfo(obj2);
    }

    public static void printObjectInfo(Object obj) {
        // Pattern Matching for instanceof (Java 16+)
        if (obj instanceof Post post) {
            System.out.println("This is a Post: " + post.getTitle() + " - Published: " + post.isPublished());
        } else if (obj instanceof User user) {
            System.out.println("This is a User: " + user.getName() + " - Email: " + user.getEmail());
        } else {
            System.out.println("Unknown object type: " + obj.getClass().getSimpleName());
        }
    }
}

