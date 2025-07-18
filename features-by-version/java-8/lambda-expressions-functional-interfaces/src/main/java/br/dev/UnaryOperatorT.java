package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class UnaryOperatorT {



    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // UnaryOperator<T>: Uppercase username
        UnaryOperator<String> toUpperCase = name -> name.toUpperCase();
        userList.forEach(user ->
                System.out.println("Upper: " + toUpperCase.apply(user.getName()))
        );

        // Trim post content
        UnaryOperator<Post> trimContent = post -> {
            post.setContent(post.getContent().trim());
            return post;
        };
        userList.stream().flatMap(user -> user.getPosts().stream())
                .forEach(post -> {
                    trimContent.apply(post);
                    System.out.println("Trimmed content: " + post.getContent());
                });

        //  Add a tag to the title
        UnaryOperator<Post> tagTitle = post -> {
            post.setTitle("[FEATURED] " + post.getTitle());
            return post;
        };
        userList.stream().flatMap(user -> user.getPosts().stream())
                .forEach(post -> {
                    tagTitle.apply(post);
                    System.out.println("Tagged title: " + post.getTitle());
                });

        // Normalize user name
        UnaryOperator<User> normalizeName = user -> {
            user.setName(Character.toUpperCase(user.getName().charAt(0)) + user.getName().substring(1).toLowerCase());
            return user;
        };
        userList.stream().forEach(user -> {
            normalizeName.apply(user);
            System.out.println("Normalized user name: " + user.getName());
        });

        // Add default post if user has none
        UnaryOperator<User> ensureHasPost = user -> {
            if (user.getPosts().isEmpty()) {
                user.getPosts().add(new Post("Welcome", "This is your first post."));
            }
            return user;
        };
        User newUser = new User("bob", "bob@gmail.com" + new ArrayList<>());
        userList.add(newUser);
        userList.forEach(user -> {
            ensureHasPost.apply(user);
            System.out.println("User " + user.getName() + " has posts: " + user.getPosts().size());
        });
    }
}