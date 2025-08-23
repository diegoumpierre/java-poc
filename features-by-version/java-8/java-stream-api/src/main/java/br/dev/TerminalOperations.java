package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TerminalOperations {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. forEach() – perform action for each element
        System.out.println("forEach:");
        userList.get(0).getPosts().stream()
                .forEach(post -> System.out.println(" - " + post.getTitle()));

        // 2. collect() – gather into a collection
        List<String> titles = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .collect(Collectors.toList());
        System.out.println("\ncollect(): " + titles);

        // 3. count() – number of elements
        long count = userList.get(0).getPosts().stream()
                .filter(post -> post.getTitle().equals("Java 8"))
                .count();
        System.out.println("\ncount(): " + count);

        // 4. anyMatch() – check if any match condition
        boolean hasJava17 = userList.get(0).getPosts().stream()
                .anyMatch(post -> post.getTitle().equals("Java 17"));
        System.out.println("\nanyMatch(Java 17): " + hasJava17);

        // 5. allMatch() – check if all match condition
        boolean allHaveTitle = userList.get(0).getPosts().stream()
                .allMatch(post -> !post.getTitle().isEmpty());
        System.out.println("allMatch(title not empty): " + allHaveTitle);

        // 6. noneMatch() – check if none match condition
        boolean noEmptyContent = userList.get(0).getPosts().stream()
                .noneMatch(post -> post.getContent().isEmpty());
        System.out.println("noneMatch(empty content): " + noEmptyContent);

        // 7. findFirst() – get first element
        Optional<Post> first = userList.get(0).getPosts().stream().findFirst();
        first.ifPresent(post -> System.out.println("\nfindFirst(): " + post.getTitle()));

        // 8. findAny() – get any element (especially useful in parallel streams)
        Optional<Post> any = userList.get(0).getPosts().parallelStream().findAny();
        any.ifPresent(post -> System.out.println("findAny(): " + post.getTitle()));

        // 9. reduce() – combine elements into one
        Optional<String> combinedTitles = userList.get(0).getPosts().stream()
                .map(Post::getTitle)
                .reduce((a, b) -> a + " | " + b);
        combinedTitles.ifPresent(s -> System.out.println("\nreduce(): " + s));
    }


}
