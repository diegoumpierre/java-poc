package com.poc.account.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Account {
    private String id;
    private String name;
    private AccountTypeEnum accountTypeEnum;


}
