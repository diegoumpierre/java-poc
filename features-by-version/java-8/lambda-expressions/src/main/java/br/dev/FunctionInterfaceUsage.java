package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.Random;
import java.util.function.*;


/**
 * | Interface           | Description                      | Example                                |
 * | ------------------- | -------------------------------- | -------------------------------------- |
 * | `Function<T,R>`     | Transform input to output        | `u -> u.getName()`                     |
 * | `Predicate<T>`      | Boolean condition check          | `u -> u.isActive()`                    |
 * | `Consumer<T>`       | Process without returning        | `u -> System.out.println(u.getName())` |
 * | `Supplier<T>`       | Generate a value without input   | `() -> new User(...)`                  |
 * | `BiFunction<T,U,R>` | Use two inputs to produce result | `(u, t) -> new Post(t, u)`             |
 * | `UnaryOperator<T>`  | Unary operation of same type     | `s -> s.toUpperCase()`                 |
 * | `BinaryOperator<T>` | Combine two of same type         | `(a, b) -> a + " & " + b`              |
 */


public class FunctionInterfaceUsage {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Function<T, R>: Convert User to String (name)
        Function<User, String> nameAndEmailFunction = user -> user.getName() +" - "+ user.getEmail();
        userList.forEach(user -> System.out.println("Name and Email: " + nameAndEmailFunction.apply(user)));

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
        BiFunction<User, String, Post> createPost = (user, title) -> new Post(title, user);
        Post post = createPost.apply(users.get(0), "Hello Lambda");
        System.out.println("Post by " + post.getAuthor().getName() + ": " + post.getTitle());

        // ✅ UnaryOperator<T>: Uppercase user name
        UnaryOperator<String> toUpperCase = name -> name.toUpperCase();
        System.out.println("Upper: " + toUpperCase.apply(users.get(0).getName()));

        // ✅ BinaryOperator<T>: Combine names
        BinaryOperator<String> concatNames = (n1, n2) -> n1 + " & " + n2;
        System.out.println("Pair: " + concatNames.apply(users.get(0).getName(), users.get(1).getName()));



    }

}
