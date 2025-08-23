package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * ✅ BiFunction<T, U, R>
 * Takes two inputs, one of type T and one of type U
 *Returns a result of type R
 * Used for composing two inputs into a single output, like merging or formatting
 */
public class BiFunctionTUR {


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        //combine User and Post using BiFunction<T, U, R>
        BiFunction<User, String, Post> createPost = (user, title) -> new Post(title, user.getName() + "-" + user.getEmail(), false);
        userList.forEach(user -> {
            Post post = createPost.apply(user, "title for " + user.getName());
            user.getPosts().add(post);
        });

        // Concatenate post-title and content
        BiFunction<String, String, String> concatPost = (title, content) -> title + ": " + content;
        userList.forEach(user -> user.getPosts().stream().forEach(post -> System.out.println(concatPost.apply(post.getTitle(), post.getContent()))));

        //Compare post content lengths
        BiFunction<Post, Post, Integer> compareByLength = (post1, post2) -> Integer.compare(
                post1.getContent().length(), post2.getContent().length());
        Post post = new Post("Not Short one", "Hi, everything is fine?");
        Post shortPost = new Post("Short", "Hi");
        System.out.println(compareByLength.apply(post, shortPost)); // > 0

        // Merge two users’ posts into a single list size
        BiFunction<User, User, Integer> totalPosts = (user1, user2) -> user1.getPosts().size() + user2.getPosts().size();
        User user = new User("Andrea", "andrea@test.com", Arrays.asList(
                new Post("Threads", "Working with Threads API"),
                new Post("Threads2", "Working with Threads API2"),
        new Post("Threads", "Working with Threads API")
        ));
        User user2 = new User("Bob", "bob@test.com", Arrays.asList(
                new Post("Streams", "Working with Stream API")
        ));
        System.out.println("Total posts: " + totalPosts.apply(user, user2));

        // Check if a user's post-title matches a given string
        BiFunction<User, String, Boolean> hasPostWithTitle = (user1, title) ->
                user1.getPosts().stream().anyMatch(post1 -> post1.getTitle().equalsIgnoreCase(title));
        System.out.println("Has post titled 'Java 8': " + hasPostWithTitle.apply(user, "Java 8"));
    }
}
