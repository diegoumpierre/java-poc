package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class StreamCreation {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();



        // 1. Stream from List
        Stream<Post> streamFromList = userList.get(0).getPosts().stream();
        streamFromList.forEach(post -> System.out.println("List Stream: " + post.getTitle()));

        // 2. Stream from Array
        Post[] postArray = {
                userList.get(0).getPosts().get(0),
                userList.get(0).getPosts().get(1)
        };
        Stream<Post> streamFromArray = Arrays.stream(postArray);
        streamFromArray.forEach(post -> System.out.println("Array Stream: " + post.getTitle()));

        // 3. Stream.of(...) from values
        Stream<String> titles = Stream.of("Java 8", "Java 11", "Java 17");
        titles.forEach(title -> System.out.println("Stream.of: " + title));

        // 4. Stream.generate(...) – infinite stream, use limit!
        Stream<Double> randomStream = Stream.generate(Math::random).limit(3);
        randomStream.forEach(randon -> System.out.println("Random: " + randon));

        // 5. Stream.iterate(...) – infinite sequence (e.g. IDs)
        Stream<Integer> idStream = Stream.iterate(1000, id -> id + 10).limit(5);
        idStream.forEach(id -> System.out.println("ID: " + id));

        // 6. Empty Stream
        Stream<Post> emptyStream = Stream.empty();
        System.out.println("Empty stream count: " + emptyStream.count());

        // 7. Stream from Set
        Set<String> tags = new HashSet<>(Arrays.asList("Java", "Streams", "Collectors"));
        tags.stream().forEach(tag -> System.out.println("Tag: " + tag));

        // 8. Stream from Map (keys, values, or entries)
        Map<String, Post> postMap = new HashMap<>();
        postMap.put("first", new Post("Intro", "Welcome"));
        postMap.put("second", new Post("Next", "Continue"));

        postMap.entrySet().stream().forEach(e ->
                System.out.println("Map entry: " + e.getKey() + " → " + e.getValue().getTitle())
        );

    }

}
