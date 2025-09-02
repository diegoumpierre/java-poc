package br.dev;

import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class VarLambdaExamples {
    public static void printUserNames(List<User> users) {
        // Using var in forEach lambda
        users.forEach((var user) -> System.out.println(user.getName()));
    }

    public static void printAllPostTitles(List<User> users) {
        // Nested var in lambdas
        users.forEach((var user) ->
            user.getPosts().forEach((var post) ->
                System.out.println(post.getTitle())
            )
        );
    }

    public static List<Post> getPublishedPosts(User user) {
        // Using var in filter
        return user.getPosts().stream()
            .filter((var post) -> post.isPublished())
            .collect(Collectors.toList());
    }

    public static List<String> getUserEmails(List<User> users) {
        // Using var in map
        return users.stream()
            .map((var user) -> user.getEmail())
            .collect(Collectors.toList());
    }

    public static List<String> getAllPublishedPostTitles(List<User> users) {
        // Combining var in flatMap and filter
        return users.stream()
            .flatMap((var user) -> user.getPosts().stream())
            .filter((var post) -> post.isPublished())
            .map((var post) -> post.getTitle())
            .collect(Collectors.toList());
    }
}

