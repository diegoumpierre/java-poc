package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.function.Consumer;

/**
 * ✅ Consumer<T>
 * A Consumer<T> represents an operation that:
 * Takes one input of type T
 * Returns no result (void)
 * Commonly used for performing actions on objects, like printing or modifying them
 */
public class ConsumerT {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Consumer<T>: Print user info
        Consumer<User> printUser = user -> System.out.println("User: " + user.getName() + ", Number of posts: " + user.getPosts().size());
        userList.forEach(printUser);

        // Print username
        Consumer<User> printUserName = user -> System.out.println("User: " + user.getName());
        userList.forEach(user -> printUserName.accept(user));


        // Print each post's title
        Consumer<User> printPostTitles = user -> user.getPosts().forEach(post -> System.out.println("Title: " + post.getTitle()));
        userList.forEach(user -> printPostTitles.accept(user));

        // Print post summary (title + first 15 characters of content)
        Consumer<Post> printSummary = post -> {
            String summary = post.getContent().length() > 15 ? post.getContent().substring(0, 15) + "..." : post.getContent();
            System.out.println(post.getTitle() + ": " + summary);
        };
        userList.forEach(user -> user.getPosts().forEach(post -> printSummary.accept(post)));


        // Modify post-content to add a tag
        Consumer<Post> addTagToContent = post -> post.setContent(post.getContent() + " #Java8");
        userList.forEach(user -> user.getPosts().forEach(addTagToContent));

        //Print posts after modification
        System.out.println("\nAfter tagging:");
        userList.forEach(user -> user.getPosts().forEach(addTagToContent));
        userList.forEach(user -> user.getPosts().forEach(post -> printSummary.accept(post)));

        // Chain Consumers (print name then titles)
        Consumer<User> printAll = printUserName.andThen(printPostTitles);
        userList.forEach(user -> printAll.accept(user));
    }
}
