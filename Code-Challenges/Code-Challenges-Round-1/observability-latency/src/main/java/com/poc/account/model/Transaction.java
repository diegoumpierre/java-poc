package com.poc.account.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Transaction {
    private String id;
    private Account account;
    private Date dt_ini;
    private String description;
    private Category category;
    private Double amount;

}
