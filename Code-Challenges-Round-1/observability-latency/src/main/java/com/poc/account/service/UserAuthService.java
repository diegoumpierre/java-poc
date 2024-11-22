package com.poc.account.service;

import com.poc.account.dao.UserAuthDao;
import com.poc.account.model.UserAuth;

import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class UserAuthService {

    private UserAuthDao userAuthDao;

    public UserAuthService(UserAuthDao userAuthDao) {
        this.userAuthDao = userAuthDao;
    }

    public Boolean createUserAuth(UserAuth userAuth){
        if (null == userAuth || null == userAuth.getNome() || userAuth.getNome().isEmpty()){
            return false;
        }
        userAuthDao.save(userAuth);
        return true;
    }

    public Boolean processUserAuth(List<UserAuth> userAuthList) throws InterruptedException {
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
                    return false;
                }
                this.userAuthDao.save(userAuth);
            }
        }
        return true;
    }

}
