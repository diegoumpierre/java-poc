package br.dev;

import br.dev.domain.User;
import br.dev.domain.Post;
import br.dev.domain.DataService;

import java.util.*;

public class PatternMatchingForCollections {
    public static String describe(Object obj) {
        // Hypothetical Java 25 syntax for pattern matching collections
        return switch (obj) {
            case List(Post p1, Post p2) when p1.isPublished() && !p2.isPublished() ->
                "Published and Draft Posts: " + p1.getTitle() + ", " + p2.getTitle();
            case List(User u1, User u2) ->
                "Two users: " + u1.getName() + ", " + u2.getName();
            case Map(String key, User user) when user.getPosts().size() > 0 ->
                "User with posts: " + key;
            default -> "Unknown collection";
        };
    }

    public static void main(String[] args) {
        DataService dataService = new DataService();
        List<User> users = dataService.getUserWithPost();
        User john = users.get(0);
        User jane = users.get(1);

        List<Post> posts = john.getPosts().subList(0, 2); // First two posts
        List<User> userList = Arrays.asList(john, jane);
        Map<String, User> userMap = new HashMap<>();
        userMap.put("admin", john);
        userMap.put("editor", jane);

        System.out.println(describe(posts)); // Should match List(Post, Post)
        System.out.println(describe(userList)); // Should match List(User, User)
        System.out.println(describe(userMap)); // Should match Map(String, User)
        System.out.println(describe(Collections.emptyList())); // Should hit default
    }
}

