package com.poc.account.dao;

import com.poc.account.model.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountDao {

    private static final List<Account> acountList = new ArrayList<>();


    public List<Account> getAll(){
        return acountList;
    }

    public void insert(Account account){
        acountList.add(account);
    }



}
