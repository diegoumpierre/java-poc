package com.poc.library.model;

import lombok.Data;

@Data
public class BookSubCategory {
    private String aid;
    private String name;
    private BookCategory category;
}
