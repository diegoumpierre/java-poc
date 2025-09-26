package br.dev;

import br.dev.domain.User;
import br.dev.domain.DataService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.lang.ScopedValue;

public class ScopedValuesExample {
    // Define a ScopedValue for the current user
    private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        DataService dataService = new DataService();
        List<User> users = dataService.getUserWithPost();

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (User user : users) {
            // Bind the ScopedValue to the user for this virtual thread
            executor.submit(() -> ScopedValue.runWhere(CURRENT_USER, user, () -> {
                System.out.println("Current user in thread: " + CURRENT_USER.get().getName());
                System.out.println("Posts:");
                for (var post : CURRENT_USER.get().getPosts()) {
                    System.out.println("  - " + post.getTitle() + (post.isPublished() ? " (published)" : " (draft)"));
                }
            }));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}

