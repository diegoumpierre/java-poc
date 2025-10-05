package br.dev;

import br.dev.domain.User;
import br.dev.domain.Post;
import br.dev.domain.DataService;
import java.util.List;

public class StringTemplatesExample {
    public static void main(String[] args) {
        DataService dataService = new DataService();
        List<User> users = dataService.getUserWithPost();
        User john = users.get(0);
        Post firstPost = john.getPosts().get(0);

        // Java 25 String Templates (Stable)
        String userInfo = STR."User: {john.getName()}, Email: {john.getEmail()}";
        String postInfo = STR."Post: {firstPost.getTitle()} | Published: {firstPost.isPublished()}";
        String summary = STR."{john.getName()} has {john.getPosts().size()} posts.";

        System.out.println(userInfo);
        System.out.println(postInfo);
        System.out.println(summary);
    }
}

