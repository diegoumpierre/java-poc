package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.*;
import java.util.function.Supplier;

public class SuplierT {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. Create a default user
        Supplier<User> defaultUser = () -> new User("Guest", "something@gmail.com");
        User guest = defaultUser.get();
        System.out.println("Default User: " + guest.getName());

        // 2. Generate a random post
        Supplier<Post> randomPost = () -> {
            String title = "Post #" + new Random().nextInt(1000);
            String content = "Random content at " + new Date();
            return new Post(title, content, true);
        };
        Post post = randomPost.get();
        System.out.println("Random Post: " + post.getTitle());

        // 3. Supply an empty list of posts
        Supplier<List<Post>> emptyPosts = ArrayList::new;
        List<Post> posts = emptyPosts.get();
        System.out.println("Empty posts? " + posts.isEmpty());

        // 4. Supply default content for a post
        Supplier<String> defaultContent = () -> "No content provided.";
        Post post1 = new Post("Untitled", defaultContent.get(), true);
        System.out.println("Post with default content: " + post1.getContent());

        // 5. Create a fixed user with predefined posts
        Supplier<User> predefinedUser = () -> {
            Post post2 = new Post("Welcome", "Welcome to the platform!", true);
            Post post3 = new Post("Rules", "Please follow the rules.", false);
            return new User("Admin", "email@email.com", Arrays.asList(post1, post2));
        };
        User admin = predefinedUser.get();
        System.out.println("Predefined User: " + admin.getName());
        admin.getPosts().forEach(p -> System.out.println(" - " + p.getTitle()));
    }
}
