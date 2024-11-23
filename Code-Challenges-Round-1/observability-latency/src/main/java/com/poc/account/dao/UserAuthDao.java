package com.poc.account.dao;

import com.poc.account.model.UserAuth;
import com.poc.observability.service.CollectedService;
import com.poc.observability.service.CollectedServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class UserAuthDao {
    private static List<UserAuth> userAuthList = new ArrayList<>();
    private CollectedService collectedService = new CollectedServiceImpl();

    public UserAuth save(UserAuth userAuth){
        collectedService.start("UserAuthDao.save");
        userAuthList.add(userAuth);
        collectedService.end("UserAuthDao.save",true);
        return userAuth;
    }

    public List<UserAuth> findAll() {
        collectedService.start("UserAuthDao.save");
        collectedService.end("UserAuthDao.save",true);
        return userAuthList;
    }
}
