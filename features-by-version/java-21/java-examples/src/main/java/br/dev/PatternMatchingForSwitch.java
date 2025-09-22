package br.dev;

import br.dev.domain.Post;
import br.dev.domain.User;
import br.dev.domain.DataService;

public class PatternMatchingForSwitch {
    public static String describe(Object obj) {
        return switch (obj) {
            case Post post when post.isPublished() -> "Published Post: '" + post.getTitle() + "'";
            case Post post -> "Draft Post: '" + post.getTitle() + "'";
            case User user when !user.getPosts().isEmpty() -> "User: " + user.getName() + " with " + user.getPosts().size() + " posts";
            case User user -> "User: " + user.getName() + " with no posts";
            default -> "Unknown object";
        };
    }

    public static void main(String[] args) {
        DataService dataService = new DataService();
        var users = dataService.getUserWithPost();
        for (User user : users) {
            System.out.println(describe(user));
            for (Post post : user.getPosts()) {
                System.out.println("  - " + describe(post));
            }
        }
        // Try with an unknown type
        System.out.println(describe("Just a string"));
    }
}

