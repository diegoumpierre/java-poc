package br.dev;

import java.util.concurrent.StructuredTaskScope;

public class StructuredConcurrencyDemo {
    public static void main(String[] args) throws InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var fastTask = scope.fork(() -> {
                Thread.sleep(200);
                return "Fast task completed by " + Thread.currentThread().getName();
            });
            var slowTask = scope.fork(() -> {
                Thread.sleep(1000);
                return "Slow task completed by " + Thread.currentThread().getName();
            });
            scope.join();
            scope.throwIfFailed();
            System.out.println(fastTask.get());
            System.out.println(slowTask.get());
        }
        // All threads and resources are cleaned up here
    }
}

