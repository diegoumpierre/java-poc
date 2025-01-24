package com.poc.rendertemplate.service;

import com.poc.rendertemplate.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserService {


    public void allUsers(PrintFormatEnum printFormatEnum){
        printFormatEnum.getPrintApplication().printFile(getAllUsers());
    }



    //simulation for a database
    private List<User> getAllUsers(){
        List<User> lst = new ArrayList<>();
        Random random = new Random(40);


        lst.add(User.builder()
                .age(random.nextInt())
                .email("joe"+random.nextInt()+"@test.com")
                .name("joe"+random.nextInt())
                .build());
        lst.add(User.builder()
                .age(random.nextInt())
                .email("juli"+random.nextInt()+"@test.com")
                .name("Juli"+random.nextInt())
                .build());
        lst.add(User.builder()
                .age(random.nextInt())
                .email("tim"+random.nextInt()+"@test.com")
                .name("Tim"+random.nextInt())
                .build());
        return lst;
    }


}
