package com.poc.account.dao;

import com.poc.account.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    private static final List<Transaction> transactionList = new ArrayList<>();

    public List<Transaction> getAll(){
        return transactionList;
    }


    public void insert(Transaction transaction){
        transactionList.add(transaction);
    }


}
