package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.Comparator;
import java.util.List;

public class MethodReference {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();


        //sorting user list by name using method reference
        userList.sort(Comparator.comparing(User::getName)); //this is void
        userList.forEach(user -> System.out.println("--> " + user.getName())); // print name

        //sorting by post-name
        userList.stream()
                .flatMap(user -> user.getPosts().stream())
                .sorted(Comparator.comparing(Post::getTitle)) // this returns a stream
                .forEach(post -> System.out.println("Post Title: " + post.getTitle())); // print post title
    }

}
