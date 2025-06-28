package com.poc.library.model;

import lombok.Data;

@Data
public class Book {
    private String aid;
    private String name;

    private BookCategory category;
    private BookSubCategory subCategory;

    private Double lateFee;
    private int  daysForRental;


}
