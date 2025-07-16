package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FunctionTR {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();


        // Function<T, R>: Convert User to String (name)
        Function<User, String> nameAndEmailFunction = user -> user.getName() +" - "+ user.getEmail();
        userList.forEach(user -> System.out.println("Name and Email: " + nameAndEmailFunction.apply(user)));

        // Get user's name
        Function<User, String> getName = user -> user.getName();
        userList.forEach(user -> System.out.println("Name: " + getName.apply(user)));

        // Get first post title from user
        Function<User, String> firstPostTitle = user -> {
            if (user.getPosts() == null || user.getPosts().isEmpty()) {
                return "No posts available";
            }
            return user.getPosts().get(0).getTitle();
        };
        userList.forEach(user -> System.out.println("First post title: " + firstPostTitle.apply(user)));

        // Count the number of posts
        Function<User, Integer> postCount = user -> user.getPosts().size();
        userList.forEach(user -> System.out.println("Post count: " + postCount.apply(user)));


        // Post summary (title + truncated content)
        Function<Post, String> summary = post -> post.getTitle() + ": " + post.getContent().substring(0, 10) + "...";
        userList.stream()
                .flatMap(user -> user.getPosts()
                        .stream())
                        .forEach(post -> System.out.println("Post Summary: " + summary.apply(post)));


        // Extract all titles from user's posts
        Function<User, List<String>> titles = user -> user.getPosts()
                .stream()
                .map(Post::getTitle)
                .collect(Collectors.toList());
        userList.stream().forEach(user -> System.out.println("Titles: " + titles.apply(user)));

        // Get post-content length
        Function<Post, Integer> length = post -> post.getContent().length();
        userList.stream().map(user -> user.getPosts()
                .stream()
                .map(post -> {
                    System.out.println("Post length: " + length.apply(post));
                    return length.apply(post);
                }));


        // Convert user to a Map summary
        Function<User, Map<String, Object>> userSummary = user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", user.getName());
            map.put("postCount", user.getPosts().size());
            return map;
        };
        userList.forEach(user -> System.out.println("User summary: " + userSummary.apply(user)));

    }
}
