package com.poc.account.dao;

import com.poc.account.model.UserAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class UserAuthDaoTest {

    private UserAuthDao userAuthDao;

    @BeforeEach
    void setUp(){
        this.userAuthDao = new UserAuthDao();
    }


    @Test
    void saveAccountShouldBeSucess(){
        //given
        UserAuth userAuth = new UserAuth();
        userAuth.setNome("name 1");

        UserAuth userAuth1 = new UserAuth();
        userAuth1.setNome("name 2");

        List<UserAuth> expectedUserAuthList = new ArrayList<>();
        expectedUserAuthList.add(userAuth);
        expectedUserAuthList.add(userAuth1);

        //when
        userAuthDao.insert(userAuth);
        userAuthDao.insert(userAuth1);

        //method under test
        assertArrayEquals(expectedUserAuthList.toArray(),userAuthDao.getAll().toArray());

    }


}