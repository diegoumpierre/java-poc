package com.poc.account.service;
import com.poc.account.dao.UserAuthDao;
import com.poc.account.model.UserAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthServiceTest {

    private UserAuthDao userAuthDao;
    private UserAuthService userAuthService;

    @BeforeEach
    void setUp(){
        this.userAuthDao = new UserAuthDao();
        this.userAuthService = new UserAuthService(userAuthDao);
    }

    @Test
    void createUserShouldBeSuccess(){
        //given
        UserAuth userAuth = new UserAuth();
        userAuth.setNome("");

        UserAuth userAuth2 = new UserAuth();
        userAuth2.setNome(null);

        UserAuth userAuth3 = new UserAuth();
        userAuth3.setNome("Umpierre");

        //when

        //method under test
        assertFalse(userAuthService.createUserAuth(null));
        assertFalse(userAuthService.createUserAuth(userAuth));
        assertFalse(userAuthService.createUserAuth(userAuth2));
        assertTrue(userAuthService.createUserAuth(userAuth3));
    }

    @Test
    void processListShouldBeSucess() throws InterruptedException {
        //given
        UserAuth userAuth = new UserAuth();
        userAuth.setNome("name 1");

        UserAuth userAuth1 = new UserAuth();
        userAuth1.setNome("name 2");

        List<UserAuth> userAuthList = new ArrayList<>();
        userAuthList.add(userAuth);
        userAuthList.add(userAuth1);

        //when
        //method under test
        assertFalse(userAuthService.processUserAuth(null));
        assertTrue(userAuthService.processUserAuth(userAuthList));
        assertEquals(1,this.userAuthDao.findAll().size());
    }

}