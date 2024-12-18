package com.poc.observability.service;

import com.poc.account.model.UserAuth;
import com.poc.account.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class CollectedServiceImplTest {

    private final int NUM_ELEMENTOS = 3;

    private UserAuthService userAuthService;
    private CollectedService collectedService;

    @BeforeEach
    void setUp(){
        userAuthService = new UserAuthService();
        collectedService = new CollectedServiceImpl();
    }

    @Test
    void shouldSuccessAnalitics() throws InterruptedException {
        List<UserAuth> userAuthList = createData();

        userAuthList.stream()
            .forEach(userAuth -> {
                userAuthService.createUserAuth(userAuth);
            });

        userAuthService.processUserAuth(userAuthList);

        collectedService.printObservability();


    }




    private List<UserAuth> createData(){

        List<UserAuth> userAuthList = new ArrayList<>();
        UserAuth userAuth = null;

        for (int i = 0; i < NUM_ELEMENTOS; i++) {
            userAuth = new UserAuth();
            userAuth.setNome("random name"+1);

            //Age
            if (i < 100){
                userAuth.setAge(i);
            }
            if (i < 200){
                userAuth.setAge(i-100);
            }

            //Email
            if (i < 300){
                userAuth.setEmail("random"+i+"@test.com");
            }
            userAuthList.add(userAuth);
        }
        return userAuthList;
    }

}
