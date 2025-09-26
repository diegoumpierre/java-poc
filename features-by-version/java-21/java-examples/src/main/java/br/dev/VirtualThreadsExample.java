package br.dev;

import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadsExample {
    public static void main(String[] args) {
        // Sample users and posts
        User john = new User("John Doe", "john@example.com", List.of(
                new Post("First Post", "Content 1", true),
                new Post("Second Post", "Content 2", false),
                new Post("Third Post", "Content 3", true)
        ));
        User jane = new User("Jane Doe", "jane@example.com", List.of(
                new Post("Jane's First Post", "Content A", false),
                new Post("Jane's Second Post", "Content B", true)
        ));

        List<User> users = List.of(john, jane);

        // Virtual thread executor
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (User user : users) {
                executor.submit(() -> {
                    System.out.println("User: " + user.getName() + " (" + user.getEmail() + ")");
                    for (Post post : user.getPosts()) {
                        String status = post.isPublished() ? "Published" : "Draft";
                        System.out.println("  - " + status + " Post: '" + post.getTitle() + "'");
                    }
                });
            }
        }
    }
}

