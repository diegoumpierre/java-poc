package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class OnetimeUse {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        //Create a Stream from users
        Stream<User> userStream = userList.stream();

        // First terminal operation - allowed
        long count = userStream.count();
        System.out.println("Total users: " + count);

        // Trying to reuse the same stream will cause IllegalStateException
        try {
            userStream.map(User::getName).forEach(System.out::println);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Correct approach: create a new stream
        userList.stream()
                .map(User::getName)
                .forEach(name -> System.out.println("User: " + name));

        // Another example: processing posts
        Stream<Post> postsStream = userList.stream()
                .flatMap(user -> user.getPosts().stream());

        // First usage - terminal operation
        Optional<Post> firstPost = postsStream.findFirst();
        firstPost.ifPresent(p -> System.out.println("\nFirst post: " + p.getTitle()));

        // eusing postsStream will fail
        try {
            postsStream.forEach(post -> System.out.println(post.getTitle()));
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Create a new stream if you need to process again
        System.out.println("\nAll post titles:");
        userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .forEach(post -> System.out.println(" - " + post.getTitle()));
    }
}
