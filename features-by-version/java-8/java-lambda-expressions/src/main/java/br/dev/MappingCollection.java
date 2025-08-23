package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class MappingCollection {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();


        //get all post-titles using a method reference
        List<String> postTitles = userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .map(post -> post.getTitle())
                .collect(Collectors.toList());


        //get all post-titles using a method reference
        List<String> authors = userList.stream()
                .map(user -> user.getName())
                .collect(Collectors.toList());

        System.out.println("Post Titles:");
        postTitles.forEach(System.out::println);

        System.out.println("\nAuthor Names:");
        authors.forEach(System.out::println);

    }

}
