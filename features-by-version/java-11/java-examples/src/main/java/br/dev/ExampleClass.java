package br.dev.domain;

import java.util.ArrayList;
import java.util.List;

public class ExampleClass {
    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();


    }

}