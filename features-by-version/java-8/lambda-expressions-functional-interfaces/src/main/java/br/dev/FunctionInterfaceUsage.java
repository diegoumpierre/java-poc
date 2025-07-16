package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.Random;
import java.util.function.*;

public class FunctionInterfaceUsage {

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

        // Consumer<T>: Print user info
        Consumer<User> printUser = user -> System.out.println("User: " + user.getName() + ", Number of posts: " + user.getPosts().size());
        userList.forEach(printUser);

        // Supplier<T>: Generate a default user
        Supplier<User> defaultUser = () -> new User("New user", "something@gmail.com");
        System.out.println("Default User: " + defaultUser.get().getName());

        // Random user generator
        Supplier<User> randomUserSupplier = () -> {
            String[] names = {"Anna", "Ben", "Chris", "Dana"};
            String[] emails = {"some@gmail.com", "another@umpierre.com", "other@some.com"};
            String name = names[new Random().nextInt(names.length)];
            String email = emails[new Random().nextInt(emails.length)];
            return new User(name, email);
        };
        System.out.println("Random User: " + randomUserSupplier.get().getName() +" - "+ randomUserSupplier.get().getEmail());


        // BiFunction<T, U, R>: Combine user and title into a Post
        BiFunction<User, String, Post> createPost = (user, title) -> new Post(title, user.getName()+"-"+user.getEmail(),false);
        userList.forEach(user -> {
            Post post = createPost.apply(user, "title for " + user.getName());
            user.getPosts().add(post);
        });

        // UnaryOperator<T>: Uppercase username
        UnaryOperator<String> toUpperCase = name -> name.toUpperCase();
        userList.forEach(user ->
            System.out.println("Upper: " + toUpperCase.apply(user.getName()))
        );


        // BinaryOperator<T>: Combine names
        BinaryOperator<String> concatNames = (string1, string2) -> string1 + " & email: " + string2;
        userList.forEach(user ->
                System.out.println("Pair: " + concatNames.apply(user.getName(), user.getEmail()))
        );



    }

}
