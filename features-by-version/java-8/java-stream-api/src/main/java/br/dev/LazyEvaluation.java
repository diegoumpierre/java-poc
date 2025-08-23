package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LazyEvaluation {

        public static void main(String[] args) {
            //get a user list from DataService
            DataService dataService = new DataService();
            List<User> userList = dataService.getUserWithPost();

            // Setup a stream with intermediate operations
            Stream<String> stream = userList.get(0).getPosts().stream()
                    .filter(post -> {
                        System.out.println("Filtering: " + post.getTitle()); // won't print yet!
                        return !post.getContent().isEmpty();
                    })
                    .map(post -> {
                        System.out.println("Mapping: " + post.getTitle()); // won't print yet!
                        return post.getTitle().toUpperCase();
                    });

            System.out.println("No terminal operation yet — nothing printed.");

            // Now trigger the stream with a terminal operation
            System.out.println("\nTriggering terminal operation:");
            List<String> titles = stream.collect(Collectors.toList());

            // Output shows filter/map only runs when collect() is called
            System.out.println("\nCollected titles:");
            titles.forEach(System.out::println);

            //Short-circuiting example with findFirst()
            System.out.println("\nShort-circuit example with findFirst():");
            List<Post> posts = userList.get(0).getPosts();
            Optional<Post> result = posts.stream()
                    .filter(post -> {
                        System.out.println("Checking: " + post.getTitle());
                        return post.getTitle().startsWith("This");
                    })
                    .findFirst(); // stops as soon as it finds the first match

            result.ifPresent(post -> System.out.println("Matched: " + post.getTitle()));
        }
    }
