package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class InfiniteStreams {

    public static void main(String[] args) {

        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. Stream.generate() – Random Posts
        Stream<Post> randomPosts = Stream.generate(() -> {
            String suffix = UUID.randomUUID().toString().substring(0, 5);
            return new Post("Random Topic", "Content-" + suffix);
        }).limit(5);

        System.out.println("Random Posts:");
        randomPosts.forEach(post -> System.out.println(" - " + post.getTitle() + " :: " + post.getContent()));


        // 2. Stream.iterate() – Sequential Users
        Stream<User> sequentialUsers = Stream.iterate(1, id -> id + 1)
                .map(id -> new User("User" + id, String.valueOf(new ArrayList<>())))
                .limit(5);

        System.out.println("\nSequential Users:");
        sequentialUsers.forEach(u -> System.out.println(" - " + u.getName()));


        // 3. Infinite Posts with incrementing numbers
        Stream<Post> numberedPosts = Stream.iterate(1, n -> n + 1)
                .map(n -> new Post("Post #" + n, "Auto-generated content #" + n))
                .limit(5);

        System.out.println("\nNumbered Posts:");
        numberedPosts.forEach(post -> System.out.println(" - " + post.getTitle()));


        // 4. Cycling through fixed topics
        List<String> topics = Arrays.asList("Java 8", "Streams API", "Collectors");
        Stream<Post> cyclingPosts = Stream.iterate(0, i -> i + 1)
                .map(i -> topics.get(i % topics.size()))
                .map(topic -> new Post(topic, "About " + topic))
                .limit(6);

        System.out.println("\nCycling Topic Posts:");
        cyclingPosts.forEach(p -> System.out.println(" - " + p.getTitle() + " :: " + p.getContent()));

        // 5. Combining Streams
        Stream<Post> combinedPosts = Stream.concat(
                userList.get(0).getPosts().stream(),
                Stream.generate(() -> {
                    String suffix = UUID.randomUUID().toString().substring(0, 5);
                    return new Post("Combined Post", "Content-" + suffix);
                }).limit(3)
        );
        System.out.println("\nCombined Posts:");
        combinedPosts.forEach(post -> System.out.println(" - " + post.getTitle() +
                " :: " + post.getContent()));

    }



}
