package br.dev.concurrent;

import java.util.concurrent.StructuredTaskScope;
import br.dev.domain.User;
import br.dev.domain.Post;
import java.util.List;

public class StructuredConcurrencyExample {
    public static void main(String[] args) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var userFuture = scope.fork(() -> fetchUser());
            var postsFuture = scope.fork(() -> fetchPosts());

            scope.join(); // Wait for all tasks to complete
            scope.throwIfFailed(); // Propagate exceptions if any

            User user = userFuture.resultNow();
            List<Post> posts = postsFuture.resultNow();

            System.out.println("User: " + user.getName());
            System.out.println("Posts:");
            for (Post post : posts) {
                System.out.println("- " + post.getTitle() + " (" + (post.isPublished() ? "Published" : "Draft") + ")");
            }
        }
    }

    static User fetchUser() throws InterruptedException {
        Thread.sleep(300); // Simulate delay
        return new User("Alice", "alice@example.com");
    }

    static List<Post> fetchPosts() throws InterruptedException {
        Thread.sleep(500); // Simulate delay
        return List.of(
            new Post("Java 25 Features", "Exploring new features", true),
            new Post("Structured Concurrency", "Concurrency made easy", false)
        );
    }
}

