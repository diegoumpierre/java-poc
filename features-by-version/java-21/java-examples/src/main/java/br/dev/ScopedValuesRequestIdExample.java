package br.dev;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.lang.ScopedValue;

public class ScopedValuesRequestIdExample {
    // Define a ScopedValue for the request id
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 1; i <= 3; i++) {
            String reqId = UUID.randomUUID().toString();
            executor.submit(() -> ScopedValue.runWhere(REQUEST_ID, reqId, () -> {
                handleRequest();
            }));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }

    private static void handleRequest() {
        System.out.println("Handling request with id: " + REQUEST_ID.get());
        processBusinessLogic();
    }

    private static void processBusinessLogic() {
        System.out.println("Business logic for request id: " + REQUEST_ID.get());
    }
}

