package main.java.br.dev;

import br.dev.domain.DataService;
import br.dev.domain.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Example 1: Run a simple async task
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            System.out.println("Running async task in thread: " + Thread.currentThread().getName());
        });
        future1.join(); // Wait for completion

        // Example 2: Supply a value asynchronously
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return "Hello from CompletableFuture!";
        });
        System.out.println(future2.get());

        // Example 3: thenApply to transform result
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> 10)
                .thenApply(x -> x * 2);
        System.out.println("Result of thenApply: " + future3.get());

        // Example 4: thenAccept to consume result
        CompletableFuture<Void> future4 = CompletableFuture.supplyAsync(() -> "User count: " + userList.size())
                .thenAccept(System.out::println);
        future4.join();

        // Example 5: thenCompose to chain dependent tasks
        CompletableFuture<String> future5 = CompletableFuture.supplyAsync(() -> userList.get(0))
                .thenCompose(user -> CompletableFuture.supplyAsync(() -> user.getName() + "'s email: " + user.getEmail()));
        System.out.println(future5.get());

        // Example 6: thenCombine to combine two futures
        CompletableFuture<String> future6a = CompletableFuture.supplyAsync(() -> userList.get(0).getName());
        CompletableFuture<String> future6b = CompletableFuture.supplyAsync(() -> userList.get(1).getName());
        CompletableFuture<String> combined = future6a.thenCombine(future6b, (name1, name2) -> name1 + " & " + name2);
        System.out.println("Combined names: " + combined.get());

        // Example 7: Exception handling with exceptionally
        CompletableFuture<String> future7 = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Something went wrong!");
            return "Won't reach here";
        }).exceptionally(ex -> "Recovered from error: " + ex.getMessage());
        System.out.println(future7.get());

        // Example 8: Using handle for result or exception
        CompletableFuture<String> future8 = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("Oops");
            return "OK";
        }).handle((result, ex) -> {
            if (ex != null) return "Handled: " + ex.getMessage();
            return result;
        });
        System.out.println(future8.get());

        // Example 9: Waiting for multiple futures (allOf)
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3, future4);
        allFutures.join();
        System.out.println("All tasks completed.");

        // Example 10: Asynchronous processing of posts
        CompletableFuture<Void> postsFuture = CompletableFuture.runAsync(() -> {
            userList.forEach(user -> {
                user.getPosts().forEach(post -> {
                    System.out.println(user.getName() + " wrote post: " + post.getTitle());
                });
            });
        });
        postsFuture.join();

        // Example 11: Simulate long-running task with complete
        CompletableFuture<String> future11 = new CompletableFuture<>();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                future11.complete("Completed after delay");
            } catch (InterruptedException e) {
                future11.completeExceptionally(e);
            }
        }).start();
        System.out.println(future11.get());
    }
}
