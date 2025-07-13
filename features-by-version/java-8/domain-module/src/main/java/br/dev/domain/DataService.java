package br.dev.domain;

import java.util.ArrayList;
import java.util.List;

public class DataService {

    public List<User> getUserWithPost() {

        User johnDoe = new User("John Doe", "john@example.com");
        User janeDoe = new User("Jane Doe", "jane@gmail.com");


        johnDoe.getPosts().add(new Post("First Post", "This is the content of the first post"));
        johnDoe.getPosts().add(new Post("Second Post", "This is the content of the Second post"));
        johnDoe.getPosts().add(new Post("Third Post", "This is the content of the Third post"));

        janeDoe.getPosts().add(new Post("Jane's First Post", "This is the content of Jane's first post"));
        janeDoe.getPosts().add(new Post("Jane's Second Post", "This is the content of Jane's second post"));

        return new ArrayList() {
            {
                add(johnDoe);
                add(janeDoe);
            }
        };
    }
}