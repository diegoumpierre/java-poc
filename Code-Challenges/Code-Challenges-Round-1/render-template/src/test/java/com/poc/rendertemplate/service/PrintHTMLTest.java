package com.poc.rendertemplate.service;


import com.poc.rendertemplate.dto.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class PrintHTMLTest {


    @Test
    void printUserHtmlShouldBeSuccess(){
        //give
        List<User> lst = new ArrayList<>();
        lst.add(User.builder()
                .age(40)
                .email("joe@test.com")
                .name("joe")
                .build());
        lst.add(User.builder()
                .age(20)
                .email("juli@test.com")
                .name("Juli")
                .build());
        lst.add(User.builder()
                .age(20)
                .email("tim@test.com")
                .name("Tim")
                .build());

        //method under test
        PrintHTML printHTML = new PrintHTML();
        printHTML.printFile(lst);

    }


}