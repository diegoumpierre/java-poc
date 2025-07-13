package br.dev.domain;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;

    private List<Post> posts;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.posts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
