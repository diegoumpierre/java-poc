package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Functional Interfaces – Summary Table
 *
 * <pre>
 * | Interface        | Method Signature                       | Description                                      |
 * |------------------|-----------------------------------------|--------------------------------------------------|
 * | ValidatorUmpierre<T>     | boolean validate(T t)                  | Validates a condition on an object               |
 * | FormatterUmpierre<T>     | String format(T t)                     | Converts an object to a formatted string         |
 * | CombinerUmpierre<T>      | T combine(T a, T b)                    | Combines two objects of the same type            |
 * | CheckerUmpierre<T, U>    | boolean check(T t, U u)                | Compares two objects and returns a boolean       |
 * | ProcessorUmpierre<T>     | void process(T t)                      | Performs a side-effect on the object             |
 * | FactoryUmpierre<T>       | T create()                             | Supplies a new instance of a type                |
 * | ExtractorUmpierre<T, R>  | R extract(T t)                         | Ex*
 * </pre>
 */
public class CustomFunctionalInterfaces {

    // === Custom Functional Interfaces ===
    @FunctionalInterface
    interface ValidatorUmpierre<T> {
        boolean validate(T t);
    }

    @FunctionalInterface
    interface FormatterUmpierre<T> {
        String format(T t);
    }

    @FunctionalInterface
    interface CombinerUmpierre<T> {
        T combine(T a, T b);
    }

    @FunctionalInterface
    interface CheckerUmpierre<T, U> {
        boolean check(T t, U u);
    }

    @FunctionalInterface
    interface ProcessorUmpierre<T> {
        void process(T t);
    }

    @FunctionalInterface
    interface FactoryUmpierre<T> {
        T create();
    }

    @FunctionalInterface
    interface ExtractorUmpierre<T, R> {
        R extract(T t);
    }


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Validator
        ValidatorUmpierre<User> validName = user -> user.getName() != null && !user.getName().isEmpty();
        ValidatorUmpierre<Post> hasContent = post -> post.getContent() != null && !post.getContent().trim().isEmpty();
        System.out.println("Valid user: " + validName.validate(userList.get(0)));
        System.out.println("Post has content: " + hasContent.validate(userList.get(0).getPosts().get(0)));

        // Formatter
        FormatterUmpierre<Post> postFormatter = post -> post.getTitle() + ": " + post.getContent();
        System.out.println("Formatted post: " + postFormatter.format(userList.get(0).getPosts().get(0)));

        // Combiner
        CombinerUmpierre<Post> postMerger = (post1, post2) -> new Post(
                post1.getTitle() + " & " + post2.getTitle(),
                post1.getContent() + "\n---\n" + post2.getContent()
        );
        Post merged = postMerger.combine(userList.get(0).getPosts().get(0), userList.get(0).getPosts().get(1));
        System.out.println("Merged post title: " + merged.getTitle());

        // Checker
        CheckerUmpierre<User, Post> ownsPost = (user, post) -> user.getPosts().contains(post);
        System.out.println("User owns post: " + ownsPost.check(userList.get(0), userList.get(0).getPosts().get(0)));

        // Processor
        ProcessorUmpierre<Post> printTitle = post -> System.out.println(">> " + post.getTitle().toUpperCase());
        printTitle.process(userList.get(0).getPosts().get(0));

        // Factory
        FactoryUmpierre<User> defaultUser = () -> new User("Guest", "guest@guest.com",new ArrayList<>());
        System.out.println("Default user: " + defaultUser.create().getName());

        // Extractor
        ExtractorUmpierre<User, Integer> postCount = user -> user.getPosts().size();
        System.out.println("Post count: " + postCount.extract(userList.get(0)));
    }

}
