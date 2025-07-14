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


        //Chain filter → map → forEach
        userList.stream()
                .forEach(user -> System.out.println("--> " + user.getName())); // print name
    }

}
