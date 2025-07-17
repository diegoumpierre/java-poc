package br.dev;

import br.dev.domain.DataService;
import br.dev.domain.Post;
import br.dev.domain.User;

import java.util.List;
import java.util.Random;
import java.util.function.*;

public class FunctionInterfaceUsage {

    public static void main(String[] args) {
        //get a user list from DataService
        DataService dataService = new DataService();
        List<User> userList = dataService.getUserWithPost();




        // UnaryOperator<T>: Uppercase username
        UnaryOperator<String> toUpperCase = name -> name.toUpperCase();
        userList.forEach(user ->
            System.out.println("Upper: " + toUpperCase.apply(user.getName()))
        );


        // BinaryOperator<T>: Combine names
        BinaryOperator<String> concatNames = (string1, string2) -> string1 + " & email: " + string2;
        userList.forEach(user ->
                System.out.println("Pair: " + concatNames.apply(user.getName(), user.getEmail()))
        );



    }

}
