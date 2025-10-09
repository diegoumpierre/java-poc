package br.dev;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;

public class StructuredConcurrencyEnhancementsExample {
    public static void main(String[] args) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> userTask = scope.fork(() -> fetchUser());
            Future<List<String>> postsTask = scope.fork(() -> fetchPosts());

            scope.join(); // Wait for all tasks
            scope.throwIfFailed(); // Propagate exceptions

            String user = userTask.resultNow();
            List<String> posts = postsTask.resultNow();

            System.out.println("User: " + user);
            System.out.println("Posts: " + posts);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String fetchUser() throws InterruptedException {
        Thread.sleep(500); // Simulate delay
        return "John Doe";
    }

    private static List<String> fetchPosts() throws InterruptedException {
        Thread.sleep(800); // Simulate delay
        List<String> posts = new ArrayList<>();
        posts.add("First Post");
        posts.add("Second Post");
        return posts;
    }
}

