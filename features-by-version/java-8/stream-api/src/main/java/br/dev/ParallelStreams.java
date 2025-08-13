package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ParallelStreams {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();


        // 1. Sequential vs Parallel
        System.out.println("Sequential Processing:");
        userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .forEach(System.out::println);

        System.out.println("\nParallel Processing:");
        userList.get(0).getPosts().stream()
                .parallel()
                .map(Post::getTitle)
                .forEach(System.out::println);

        // 2. Parallel filtering and mapping
        List<String> javaTitles = userList.get(0).getPosts().stream()
                .parallel()
                .filter(p -> p.getTitle().startsWith("Java"))
                .map(Post::getTitle)
                .collect(Collectors.toList());

        System.out.println("\nPosts starting with 'Java': " + javaTitles);

        // 3. Parallel sorting
        List<Post> sortedParallel = userList.get(0).getPosts().stream().parallel()
                .sorted(Comparator.comparing(Post::getTitle))
                .collect(Collectors.toList());

        System.out.println("\nSorted in parallel:");
        sortedParallel.forEach(p -> System.out.println(" - " + p.getTitle()));

        // 4. Parallel reduction
        String combinedTitles = userList.get(0).getPosts().stream().parallel()
                .map(Post::getTitle)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + " | " + b);

        System.out.println("\nCombined Titles (Parallel Reduce): " + combinedTitles);

        // 5. Parallel processing from a User list
        long totalPosts = userList.parallelStream()
                .flatMap(user -> user.getPosts().stream())
                .count();

        System.out.println("\nTotal number of posts (Parallel Count): " + totalPosts);
    }

    // 6. Parallel processing with a custom comparator
    public static List<Post> getSortedPostsByTitle(List<User> users) {
        return users.parallelStream()
                .flatMap(user -> user.getPosts().stream())
                .sorted(Comparator.comparing(Post::getTitle))
                .collect(Collectors.toList());
    }
}