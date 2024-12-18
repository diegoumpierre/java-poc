package com.poc.account.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Category {
    private String id;
    private String name;
    private Category parent;
}
