package com.poc.account.service;

import com.poc.account.dao.AccountDao;
import com.poc.account.model.Account;

import java.util.List;

public class AccountService {

    private final AccountDao accountDao = new AccountDao();

    public void insert(Account account){
        accountDao.insert(account);
    }

    public List<Account> getAll(){
        return accountDao.getAll();
    }

}
