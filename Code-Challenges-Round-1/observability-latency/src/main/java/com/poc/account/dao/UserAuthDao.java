package com.poc.account.dao;

import com.poc.account.model.UserAuth;

import java.util.ArrayList;
import java.util.List;

public class UserAuthDao {
    private static List<UserAuth> userAuthList = new ArrayList<>();

    public UserAuth save(UserAuth userAuth){
        userAuthList.add(userAuth);
        return userAuth;
    }

    public List<UserAuth> findAll() {
        return userAuthList;
    }
}
