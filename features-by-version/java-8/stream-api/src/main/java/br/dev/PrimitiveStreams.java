package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.LongStream;

public class PrimitiveStreams {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. IntStream – Count posts per user
        System.out.println("Posts per user:");
        userList.stream()
                .mapToInt(user -> user.getPosts().size()) // IntStream
                .forEach(System.out::println);

        // 2. IntStream – Sum of all posts
        int totalPosts = userList.stream()
                .mapToInt(user -> user.getPosts().size())
                .sum();
        System.out.println("\nTotal number of posts: " + totalPosts);

        // 3. IntStream – Average posts per user
        double avgPosts = userList.stream()
                .mapToInt(user -> user.getPosts().size())
                .average()
                .orElse(0);
        System.out.println("\nAverage posts per user: " + avgPosts);

        // 4. LongStream – Sequential IDs for posts
        System.out.println("\nGenerated Post IDs:");
        LongStream.rangeClosed(1, 5)
                .mapToObj(id -> new Post("Generated Post " + id, "Content " + id))
                .forEach(post -> System.out.println(post.getTitle()));

        // 5. DoubleStream – Random scores for posts
        System.out.println("\nRandom scores for posts:");
        DoubleStream.generate(() -> Math.random() * 10)
                .limit(5)
                .forEach(score -> System.out.println(String.format("%.2f", score)));

        // 6. IntStream – Title length statistics
        IntSummaryStatistics titleStats = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .mapToInt(post -> post.getTitle().length())
                .summaryStatistics();

        System.out.println("\nTitle length statistics: " + titleStats);

    }


}
