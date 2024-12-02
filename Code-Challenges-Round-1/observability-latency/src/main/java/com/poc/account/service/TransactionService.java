package com.poc.account.service;

import com.poc.account.dao.TransactionDao;
import com.poc.account.model.Transaction;

import java.util.List;

public class TransactionService {

    private final TransactionDao transactionDao = new TransactionDao();

    public List<Transaction> getAll(){
        return transactionDao.getAll();
    }

    public void insert(Transaction transaction){
        transactionDao.insert(transaction);
    }

}
