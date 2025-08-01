package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.User;

import java.util.List;
import java.util.Optional;

public class OptionalIfPresent {


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();


        //test optional
        Optional<List<User>> optionalUsers = Optional.ofNullable(userList);
        optionalUsers.ifPresent(users ->
                users.forEach(
                        user -> System.out.println(user.getName()))
        );


        //Using Optional.ifPresent with a single User
        Optional<User> optionalUser = Optional.ofNullable(userList.get(0));
        optionalUser.ifPresent(user -> System.out.println(user.getEmail()));


        



    }


}
