package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class StreamPipelining {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        //  Stream pipeline example:
        // 1) Start with users
        // 2) Flatten to posts
        // 3) Filter posts containing "Streams" in title
        // 4) Map to uppercase titles
        // 5) Sort titles
        // 6) Collect into a list
        List<String> streamTitles = userList.stream()
                .flatMap(user -> user.getPosts().stream())        // Step 1: flatMap to Post
                .filter(post -> post.getTitle().contains("Streams")) // Step 2: filter by title
                .map(post -> post.getTitle().toUpperCase())       // Step 3: map to uppercase
                .sorted()                                   // Step 4: sort alphabetically
                .collect(Collectors.toList());              // Step 5: collect to list

        System.out.println("Filtered & processed titles:");
        streamTitles.forEach(System.out::println);

        // Another pipeline: count posts per user that mention "Java"
        userList.stream()
                .map(user -> {
                    long count = user.getPosts().stream()
                            .filter(post -> post.getTitle().contains("Java"))
                            .count();
                    return user.getName() + " has " + count + " Java-related posts";
                })
                .forEach(System.out::println);
    }

}
