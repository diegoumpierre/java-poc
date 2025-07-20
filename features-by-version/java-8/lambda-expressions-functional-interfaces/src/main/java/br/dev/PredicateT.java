package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.function.Predicate;

/**
 * ✅ Predicate<T>
 * A Predicate<T> represents a boolean-valued function of one argument
 * It is commonly used for filtering or matching conditions
 */
public class PredicateT {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Predicate<T>: Filter active users
        Predicate<Post> postIsPublished = post -> post.isPublished();
        userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .filter(postIsPublished)
                .forEach(post -> System.out.println("Post Published: " + post.getTitle()));

        // Check if user has a name
        Predicate<User> hasName = user -> user.getName() != null && !user.getName().isEmpty();
        userList.forEach(user -> System.out.println("Has name: " + hasName.test(user)));

        // Check if user has posts
        Predicate<User> hasPosts = user -> user.getPosts() != null && !user.getPosts().isEmpty();
        userList.forEach(user -> System.out.println("Has posts: " + hasPosts.test(user)));

        // Check if post has non-empty content
        Predicate<Post> hasContent = post -> post.getContent() != null && !post.getContent().isEmpty();
        userList.stream().map(post-> post.getPosts())
                .flatMap(List::stream)
                .forEach(post -> System.out.println("Post has content: " + hasContent.test(post)));

        // Check if a post's title starts with "Java"
        Predicate<Post> isJavaPost = post -> post.getTitle().startsWith("Java");
        userList.stream().map(post-> post.getPosts())
                .flatMap(List::stream)
                .forEach(post -> System.out.println("Post is Java post: " + isJavaPost.test(post)));

        // Check if a user has at least one post with content
        Predicate<User> hasPostWithContent = user -> user.getPosts().stream().anyMatch(hasContent);
        userList.forEach(user -> System.out.println("User has post with content: " + hasPostWithContent.test(user)));

        // Combine predicates: user has name AND has posts
        Predicate<User> isValidUser = hasName.and(hasPosts);
        userList.forEach(user ->  System.out.println("User is valid: " + isValidUser.test(user)));


        // Negate a predicate: user has no posts
        Predicate<User> hasNoPosts = hasPosts.negate();
        userList.forEach(user -> {
            System.out.println("User has no posts: " + hasNoPosts.test(user));
        });

    }

}
