package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;

public class ForEachLoop {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // 1. Print usernames
        userList.forEach(user -> System.out.println("User: " + user.getName()));

        // 2. Print how many posts each user has
        userList.forEach(user -> System.out.println(user.getName() + " has " + user.getPosts().size() + " posts"));

        // 3. Print all post-titles for each user
        userList.forEach(user -> {
            System.out.println("Posts by " + user.getName() + ":");
            user.getPosts().forEach(p -> System.out.println(" - " + p.getTitle()));
        });


    }

    private static void printTitle(Post post) {
        System.out.println(" -> " + post.getTitle());
    }

}
