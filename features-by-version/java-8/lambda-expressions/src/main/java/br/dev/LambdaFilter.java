package br.dev.lambda_examples;

import br.dev.domain.DataService;
import br.dev.domain.User;

import java.util.List;
import java.util.stream.Collectors;

public class LambdaFilter {


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Old way
        List<User> filteredUsers = new java.util.ArrayList<>();
        for(User user : userList) {
            if (user.getEmail().endsWith("@gmail.com")) {
                filteredUsers.add(user);
            }
        }
        System.out.println("Filtered manually:");
        for (User user: filteredUsers) {
            System.out.println(user.getName() + " - " + user.getEmail());
        }

        //Lambda way
        List<User> lambdaFilteredUsers = userList.stream()
                .filter(user -> user.getEmail().endsWith("@gmail.com"))
                .collect(Collectors.toList());
        System.out.println("Filtered with Lambda:");
        lambdaFilteredUsers.forEach(user -> System.out.println(user.getName() + " - " + user.getEmail()));

    }
}
