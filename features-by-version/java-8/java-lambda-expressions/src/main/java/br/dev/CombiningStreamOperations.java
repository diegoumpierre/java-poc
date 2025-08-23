package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;

public class CombiningStreamOperations {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();


        //Chain filter → map → forEach
        userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .filter(Post::isPublished)                         // only published posts
                .map(Post::getTitle)                               // extract title
                .forEach(title -> System.out.println("--> " + title)); // print title
    }

}
