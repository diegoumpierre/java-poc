package com.poc.account.service;

import com.poc.account.dao.UserAuthDao;
import com.poc.account.model.UserAuth;
import com.poc.observability.service.CollectedService;
import com.poc.observability.service.CollectedServiceImpl;

import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class UserAuthService {

    private UserAuthDao userAuthDao;
    private CollectedService collectedService;

    public UserAuthService() {
        this.userAuthDao = new UserAuthDao();
        this.collectedService = new CollectedServiceImpl();
    }

    public Boolean createUserAuth(UserAuth userAuth){
        collectedService.start("UserAuthService.createUserAuth");
        if (null == userAuth || null == userAuth.getNome() || userAuth.getNome().isEmpty()){
            collectedService.end("UserAuthService.createUserAuth",false);
            return false;
        }

        userAuthDao.save(userAuth);
        collectedService.end("UserAuthService.createUserAuth",true);
        return true;
    }

    public Boolean processUserAuth(List<UserAuth> userAuthList) throws InterruptedException {
        collectedService.start("UserAuthService.processUserAuth");
        if (userAuthList == null){
            return false;
        }

        Random rand = new Random();

        //make any process
        for(int i=0;i< userAuthList.size();i++){

            UserAuth userAuth = userAuthList.get(i);

            if (i % 2 == 0) {
                sleep(rand.nextInt((9000 - 1000) + 1) + 1000);
            }
            else {
                sleep(rand.nextInt((10 - 1) + 1) + 1);
                if (userAuth.getAge() > 30 && userAuth.getEmail().isEmpty()){
                    collectedService.end("UserAuthService.processUserAuth",false);
                    return false;
                }
                this.userAuthDao.save(userAuth);

            }
        }
        collectedService.end("UserAuthService.processUserAuth", true);
        return true;
    }

}
