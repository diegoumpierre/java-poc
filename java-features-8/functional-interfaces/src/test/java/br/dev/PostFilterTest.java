package br.dev;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class PostFilterTest {

    @Test
    void testFilterPosts() {
        PostFilter postFilter = post -> post.getTitle().contains("Java");

        Post post1 = new Post("Learning Java", "Alice");
        Post post2 = new Post("Understanding Python", "Bob");
        Post post3 = new Post("Java Streams", "Charlie");

        assertTrue(postFilter.filter(post1));
        assertFalse(postFilter.filter(post2));
        assertTrue(postFilter.filter(post3));
    }

    @Test
    void testAnotherExample(){
        List<Post> posts = Arrays.asList(
                new Post("Intro to Java 8", "Alice"),
                new Post("Advanced Spring Boot", "Bob"),
                new Post("JPA Tips", "Alice")
        );

        // Only posts written by Alice
        PostFilter authorIsAlice = post -> "Alice".equals(post.getAuthor());

        // Only posts with title longer than 10 characters
        PostFilter longTitle = post -> post.getTitle().length() > 10;

        List<String> alicePosts = filterPosts(posts, authorIsAlice);
        List<String> longTitlePosts = filterPosts(posts, longTitle);

        assertEquals(2, alicePosts.size());
        assertTrue(alicePosts.contains("- Intro to Java 8 by Alice"));
        assertTrue(alicePosts.contains("- JPA Tips by Alice"));

        assertEquals(2, longTitlePosts.size());
        assertTrue(longTitlePosts.contains("- Intro to Java 8 by Alice"));
        assertTrue(longTitlePosts.contains("- Advanced Spring Boot by Bob"));
    }

    private List<String> filterPosts(List<Post> posts, PostFilter filter) {
        List<String> result = new ArrayList<>();
        for (Post post : posts) {
            if (filter.filter(post)) {
                result.add("- " + post.getTitle() + " by " + post.getAuthor());
            }
        }
        return result;
    }


    @Test
    void postFilterChainingExample(){
        List<Post> posts = Arrays.asList(
                new Post("Intro to Java 8", "Alice"),
                new Post("Advanced Spring Boot", "Bob"),
                new Post("JPA", "Alice"),
                new Post("Microservices with Spring", "Charlie")
        );

        PostFilter authorIsAlice = post -> "Alice".equals(post.getAuthor());
        PostFilter titleLongerThan5 = post -> post.getTitle().length() > 5;

        PostFilter combinedFilter = authorIsAlice.and(titleLongerThan5);

        List<String> filteredPosts = filterPosts(posts, combinedFilter);
        assertEquals(1, filteredPosts.size());
        assertTrue(filteredPosts.contains("- Intro to Java 8 by Alice"));

    }

    @Test
    void streamFilterExample(){
        List<Post> posts = Arrays.asList(
                new Post("Intro to Java 8", "Alice"),
                new Post("Advanced Spring Boot", "Bob"),
                new Post("JPA", "Alice"),
                new Post("Docker Intro", "Alice")
        );

        Predicate<Post> authorIsAlice = post -> "Alice".equals(post.getAuthor());
        Predicate<Post> shortTitle = post -> post.getTitle().length() <= 10;

        List<String> result = new ArrayList<>();
        posts.stream()
                .filter(authorIsAlice.and(shortTitle))
                .forEach(post -> result.add("- " + post.getTitle() + " by " + post.getAuthor()));

        assertEquals(1, result.size());
        assertTrue(result.contains("- JPA by Alice"));


    }



}