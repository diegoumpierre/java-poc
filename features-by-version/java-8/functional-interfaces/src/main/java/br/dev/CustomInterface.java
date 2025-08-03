package br.dev;

import br.dev.domain.Post;
import br.dev.domain.User;

/**
 *  ✅ Custom functional interfaces
 *  A custom functional interface is any interface with a single
 *  abstract method and marked with @FunctionalInterface.
 *  You can use lambdas with it.
 */
public class CustomInterface {

    //Validator<T> — validate a User or Post
    @FunctionalInterface
    interface Validator<T> {
        boolean validate(T t);
    }

    // Usage:
    Validator<User> isValidUser = user -> user.getName() != null && !user.getName().isEmpty();
    Validator<Post> hasContent = post -> post.getContent() != null && !post.getContent().trim().isEmpty();

    private void usingValidator() {
        User user = new User("Alice", "some@email.com");
        Post post = new Post("Title", "Body");

        System.out.println("Valid user? " + isValidUser.validate(user));
        System.out.println("Post has content? " + hasContent.validate(post));
    }


    // Formatter<T> — convert object to String
    @FunctionalInterface
    interface Formatter<T> {
        String format(T t);
    }

    // Usage:
    Formatter<Post> postFormatter = post -> post.getTitle() + ": " + post.getContent();
    Formatter<User> userFormatter = user -> user.getName() + " (" + user.getPosts().size() + " posts)";

    private void usingFormatter() {
        Post post = new Post("Intro", "Lambda");
        User user = new User("Bob", "bo@bo.com");

        System.out.println(postFormatter.format(post));
        System.out.println(userFormatter.format(user));
    }

    // Combiner<T> — combine two objects into one
    @FunctionalInterface
    interface Combiner<T> {
        T combine(T object1, T object2);
    }

    // Usage: Combine post content
    Combiner<Post> postMerger = (post1, post2) -> new Post(
            post1.getTitle() + " & " + post1.getTitle(),
            post2.getContent() + "\n" + post2.getContent()
    );

    private void usingCombiner() {
        Post post1 = new Post("First", "Content 1");
        Post post2 = new Post("Second", "Content 2");

        Post combinedPost = postMerger.combine(post1, post2);
        System.out.println("Combined Post: " + combinedPost.getTitle() + " - " + combinedPost.getContent());
    }

    // Checker<T, U> — check if T belongs to U
    @FunctionalInterface
    interface Checker<T, U> {
        boolean check(T t, U u);
    }

    // Usage: Check if post belongs to user
    Checker<User, Post> isOwner = (user, post) -> user.getPosts().contains(post);

    private void usingChecker() {
        User user = new User("Charlie", "ch@gmail.com");
        Post post = new Post("Hello", "World");
        user.getPosts().add(post);
        System.out.println("Is user owner of post? " + isOwner.check(user, post));
    }

    // Processor<T> — process an object of type T
    @FunctionalInterface
    interface Processor<T> {
        void process(T t);
    }

    // Usage: Print post title in uppercase
    Processor<Post> printTitleUpper = post -> System.out.println(post.getTitle().toUpperCase());

    private void usingProcessor() {
        Post post = new Post("Lambda", "Functional Interfaces");
        printTitleUpper.process(post);
    }

    // Factory<T> — create an instance of T
    @FunctionalInterface
    interface Factory<T> {
        T create();
    }

    // Usage: Create default User
    Factory<User> guestFactory = () -> new User("Guest","guest@email.com");

    private void usingFactory() {
        User guest = guestFactory.create();
        System.out.println("Created user: " + guest.getName() + " with email: " + guest.getEmail());
    }

    public static void main(String[] args) {
        CustomInterface customInterface = new CustomInterface();
        customInterface.usingValidator();
        customInterface.usingFormatter();
        customInterface.usingCombiner();
        customInterface.usingChecker();
        customInterface.usingProcessor();
        customInterface.usingFactory();
    }

}
