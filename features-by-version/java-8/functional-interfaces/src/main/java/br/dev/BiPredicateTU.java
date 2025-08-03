package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * ✅ BiPredicate<T, U>
 * A BiPredicate<T, U> represents a predicate (boolean-valued function)
 * that takes two arguments of types T and U
 * Returns a boolean result
 * Commonly used for testing conditions involving two inputs
 */
public class BiPredicateTU {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Check if a user has authored a post by title match
        BiPredicate<User, String> hasPostWithTitle = (user, title) ->
                user.getPosts().stream().anyMatch(post -> post.getTitle().equalsIgnoreCase(title));
        userList.stream().forEach(
                user -> System.out.println("User: " + user.getName() + " has post with title 'Java 8': " +
                        hasPostWithTitle.test(user, "Java 8"))
        );

        // Check if a post-content is longer than a given number
        BiPredicate<Post, Integer> contentLongerThan = (post, length) -> post.getContent().length() > length;
        userList.stream().forEach(
                user -> user.getPosts().forEach(
                        post -> System.out.println("Post: " + post.getTitle() + " content > 10: " +
                                contentLongerThan.test(post, 10))
                )
        );


        // Check if a post belongs to user (by identity)
        BiPredicate<User, Post> isAuthorOf = (user, post) -> user.getPosts().contains(post);
        userList.stream().forEach(user -> {
            user.getPosts().forEach(post -> {
                System.out.println("User: " + user.getName() + " is author of post: " + post.getTitle() +
                        " - " + isAuthorOf.test(user, post));
            });
        });

        // Check if user and post-title start with the same letter
        BiPredicate<User, Post> sameInitial = (user, post) -> user.getName().charAt(0) == post.getTitle().charAt(0);
        userList.stream().forEach(user -> {
            user.getPosts().forEach(post1 -> {
                System.out.println("User: " + user.getName() + " and post: " + post1.getTitle() +
                        " start with same initial: " + sameInitial.test(user, post1));
            });
        });

        // Check if user has more than X posts
        BiPredicate<User, Integer> hasMoreThanPosts = (user, numPosts) -> user.getPosts().size() > numPosts;
        userList.forEach(user ->
            System.out.println("User: " + user.getName() + " has more than 3 post: " + hasMoreThanPosts.test(user, 3))
        );
    }


}
