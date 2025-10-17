package br.dev;

import java.util.concurrent.StructuredTaskScope;

public class ScopedThreadsExample {
    public static void main(String[] args) throws InterruptedException {
        // Example: Using a scoped thread to run a simple task
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            StructuredTaskScope.Subtask<String> subtask = scope.fork(() -> {
                Thread.sleep(500); // Simulate work
                return "Scoped thread result: " + Thread.currentThread().getName();
            });
            scope.join(); // Wait for all subtasks
            scope.throwIfFailed(); // Propagate exceptions if any
            System.out.println(subtask.get()); // Print result from scoped thread
        }
        // The thread and resources are cleaned up automatically here
    }
}

