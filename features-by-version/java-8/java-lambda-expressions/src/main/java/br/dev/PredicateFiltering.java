package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateFiltering {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. Filter posts that have content
        Predicate<Post> hasContent = post -> post.getContent() != null
                && !post.getContent().trim().isEmpty();


        List<Post> withContent = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .filter(hasContent)
                .collect(Collectors.toList());
        System.out.println("Posts with content: " + withContent.size());

        // 2. Filter posts with title containing "Java"
        Predicate<Post> titleHasJava = post -> post.getTitle().toLowerCase().contains("java");
        List<Post> javaPosts = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .filter(titleHasJava)
                .collect(Collectors.toList());
        System.out.println("Posts about Java: " + javaPosts.size());

        // 3. Filter posts with content longer than 20 characters
        Predicate<Post> longContent = post -> post.getContent().length() > 20;
        List<Post> longPosts = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .filter(longContent)
                .collect(Collectors.toList());
        System.out.println("Posts with long content: " + longPosts.size());

        // 4. Combine predicates — hasContent AND title contains "Java"
        Predicate<Post> javaWithContent = hasContent.and(titleHasJava);
        List<Post> filtered = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .filter(javaWithContent)
                .collect(Collectors.toList());
        System.out.println("Posts about Java with content: " + filtered.size());

        // 5. Negate a predicate — posts without content
        Predicate<Post> noContent = hasContent.negate();
        List<Post> emptyPosts = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .filter(noContent)
                .collect(Collectors.toList());
        System.out.println("Empty posts: " + emptyPosts.size());


    }

}
