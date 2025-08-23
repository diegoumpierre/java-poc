package main.java.br.dev;

import br.dev.domain.DataService;
import br.dev.domain.User;
import br.dev.domain.Post;

import java.util.List;
import java.util.Optional;

public class OptionalExample {
    public static void main(String[] args) {

        // Get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Example 1: Wrap a User in an Optional
        Optional<User> optionalUser = userList.stream().findFirst();
        optionalUser.ifPresent(user -> System.out.println("User name: " + user.getName()));

        // Example 2: Use Optional to get the first Post of a User safely
        Optional<Post> optionalPost = optionalUser
                .flatMap(user -> user.getPosts().stream().findFirst());
        optionalPost.ifPresent(post -> System.out.println("First post title: " + post.getTitle()));

        // Example 3: Use filter to check if the first post is published
        boolean isFirstPostPublished = optionalPost
                .filter(Post::isPublished)
                .isPresent();
        System.out.println("Is first post published? " + isFirstPostPublished);

        // Example 4: Use map to transform Optional<User> to Optional<String> (user email)
        Optional<String> email = optionalUser.map(User::getEmail);
        System.out.println("User email: " + email.orElse("No email found"));


        // Example 5: Chaining Optionals to avoid NullPointerException
        String firstPostTitle = optionalUser
                .flatMap(user -> user.getPosts().stream().findFirst())
                .map(Post::getTitle)
                .orElse("No post found");
        System.out.println("First post title (safe): " + firstPostTitle);

        // Example 6: Using orElse and orElseGet
        User fallbackUser = new User("Fallback", "fallback@email.com");
        User userOrFallback = optionalUser.orElse(fallbackUser);
        System.out.println("User or fallback: " + userOrFallback.getName());

        // Example 7: Using orElseThrow
        try {
            User userOrException = optionalUser.orElseThrow(() -> new RuntimeException("User not found!"));
            System.out.println("User found: " + userOrException.getName());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        // Example 8: Optional.empty() usage
        Optional<Post> emptyPost = Optional.empty();
        System.out.println("Empty post present? " + emptyPost.isPresent());
    }
}