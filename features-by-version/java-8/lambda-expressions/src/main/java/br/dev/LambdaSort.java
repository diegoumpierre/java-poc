package br.dev.lambda_examples;

import br.dev.domain.DataService;
import br.dev.domain.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LambdaSort {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        //Old way: using anonymous Comparator class
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User a, User b) {
                return a.getName().compareTo(b.getName());
            }
        });
        System.out.println("Sorted with Anonymous Class:");
        userList.forEach(u -> System.out.println(u.getName()));

        //Lambda way
        userList.sort((a, b) -> a.getName().compareTo(b.getName()));
        System.out.println("Sorted with Lambda:");
        userList.forEach(u -> System.out.println(u.getName()));

        //Method reference (most concise)
        userList.sort(Comparator.comparing(User::getName));
        System.out.println("Sorted with Method Reference:");
        userList.forEach(u -> System.out.println(u.getName()));
    }
}
