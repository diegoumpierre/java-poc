package com.poc.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {

    private int id;
    private String name;
    private Category parentCategory;



}
