package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.User;

import java.util.List;

public class RunnableImplementation {


    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();

        // Old way
        Runnable oldRunnable = new Runnable() {
            public void run() {
                for (User user : userList) {
                    System.out.println("User: " + user.getName() + ", Email: " + user.getEmail());
                }
                System.out.println("Running with Anonymous Class");
            }
        };
        oldRunnable.run();

        // Lambda
        Runnable lambdaRunnable = () -> {
            for (User user : userList) {
                System.out.println("User: " + user.getName() + ", Email: " + user.getEmail());
            }
            System.out.println("Running with Lambda");
        };
        lambdaRunnable.run();
    }

}
