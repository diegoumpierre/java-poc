package br.dev.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void StaticRefExample(){
        List<Post> posts = Arrays.asList(
                new Post("Java 8"),
                new Post("Java 17"),
                new Post("Java 21")
        );

        // Static method reference
        posts.forEach(Post::printStatic);
    }


    @Test
    void InstanceRefExample(){
        Post post = new Post("Hello Method Ref");
        Consumer<Void> printer = v -> post.printInstance(); // equivalent:
        Runnable runnable = post::printInstance;

        runnable.run();  // prints: Instance: Hello Method Ref
    }


    @Test
    void InstanceRefArbitraryExample(){
        List<Post> posts = Arrays.asList(
                new Post("Intro to Java"),
                new Post("Advanced Java")
        );

        // Arbitrary object method reference
        posts.stream()
                .map(Post::getTitle)
                .forEach(System.out::println);  // prints: titles
    }


    @Test
    void ConstructorRefExample(){
        Function<String, Post> postCreator = Post::new;

        Post p = postCreator.apply("Created with Constructor Ref");
        System.out.println(p.getTitle());  // prints: Created with Constructor Ref
    }

}