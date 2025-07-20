package br.dev.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class DataService {

    public Supplier<Post> randomPost = () -> {
        String title = "Post #" + new Random().nextInt(1000);
        String content = "Random content at " + new Date();
        return new Post(title, content, true);
    };


    public List<User> getUserWithPost() {

        User johnDoe = new User("John Doe", "john@example.com");
        User janeDoe = new User("Jane Doe", "jane@gmail.com");


        johnDoe.getPosts().add(new Post("First Post", "This is the content of the first post", true));
        johnDoe.getPosts().add(new Post("Second Post", "This is the content of the Second post",false));
        johnDoe.getPosts().add(new Post("Third Post", "This is the content of the Third post",true));

        janeDoe.getPosts().add(new Post("Jane's First Post", "This is the content of Jane's first post",false));
        janeDoe.getPosts().add(new Post("Jane's Second Post", "This is the content of Jane's second post", true));

        return new ArrayList() {
            {
                add(johnDoe);
                add(janeDoe);
            }
        };
    }
}