package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupingWithCollectors {


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        //1. Group all posts by title
        List<Post> allPosts = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .collect(Collectors.toList());

        Map<String, List<Post>> postsByTitle = allPosts.stream()
                .collect(Collectors.groupingBy(Post::getTitle));

        System.out.println("Grouped posts by title:");
        postsByTitle.forEach((title, posts) -> {
            System.out.println(" - " + title + ": " + posts.size() + " post(s)");
        });

        //2. Group posts by username (author)
        Map<String, List<Post>> postsByUser = userList.stream()
                .collect(Collectors.toMap(
                        User::getName,
                        User::getPosts
                ));

        System.out.println("\nGrouped posts by user:");
        postsByUser.forEach((user, posts) -> {
            System.out.println(" - " + user + ": " + posts.size() + " post(s)");
        });

        //3. Count how many posts each user has
        Map<String, Long> postCountPerUser = userList.stream()
                .collect(Collectors.toMap(
                        User::getName,
                        user -> (long) user.getPosts().size()
                ));

        System.out.println("\nPost count per user:");
        postCountPerUser.forEach((user, count) -> {
            System.out.println(" - " + user + ": " + count);
        });

        //4. Group titles by user
        Map<String, List<String>> titlesByUser = userList.stream()
                .collect(Collectors.toMap(
                        User::getName,
                        user -> user.getPosts().stream()
                                .map(Post::getTitle)
                                .collect(Collectors.toList())
                ));

        System.out.println("\nGrouped titles by user:");
        titlesByUser.forEach((user, titles) -> {
            System.out.println(" - " + user + ": " + titles);
        });
    }

}
