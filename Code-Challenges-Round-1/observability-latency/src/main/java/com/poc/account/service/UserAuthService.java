package com.poc.account.service;

import com.poc.account.dao.UserAuthDao;
import com.poc.account.model.UserAuth;

import java.util.List;

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

    public Boolean processUserAuth(List<UserAuth> userAuthList){
        if (userAuthList == null){
            return false;
        }





        return true;
    }

}
