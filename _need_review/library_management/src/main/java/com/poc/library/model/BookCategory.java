package com.poc.library.model;

import lombok.Data;

import java.util.List;

@Data
public class BookCategory {

    private String aid;
    private String name;
    private List<BookSubCategory> subCategories;

}
