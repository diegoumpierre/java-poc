package com.poc.service;

import com.poc.domain.Category;
import com.poc.domain.Fee;
import com.poc.domain.StateEnum;

import java.util.ArrayList;
import java.util.List;

public class DataTest {


    public static Fee gimmeFee(){
        return Fee.builder()
                .value(Double.valueOf(10d))
                .year(Integer.valueOf(2020))
                .stateEnum(StateEnum.RS)
                .build();
    }


    public static List<Fee> gimmeFeeList(int quantity) {

        List<Fee> feeList = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            feeList.add(Fee.builder()
                    .value(Double.valueOf(10d))
                    .year(Integer.valueOf(2020))
                    .stateEnum(StateEnum.RS)
                    .build());
        }
        return feeList;
    }



    public static List<Category> gimmeCategoryList(){
        List<Category> categoryList = new ArrayList<>();

        Category habitation = Category.builder()
                .id(1)
                .name("Habitation")
                .build();
        categoryList.add( habitation);

        Category transportation = Category.builder()
                .id(2)
                .name("Transportation")
                .build();
        categoryList.add(transportation);

        Category health = Category.builder()
                .id(3)
                .name("Health")
                .build();
        categoryList.add( health);

        Category brazil = Category.builder()
                .id(4)
                .name("Brazil")
                .parentCategory(habitation)
                .build();
        categoryList.add(brazil);
        Category eua = Category.builder()
                .id(5)
                .name("Eua")
                .parentCategory(habitation)
                .build();
        categoryList.add(eua);
        Category braziltorres = Category.builder()
                .id(6)
                .name("Torres")
                .parentCategory(brazil)
                .build();
        categoryList.add(braziltorres);
        Category brazilPoa = Category.builder()
                .id(7)
                .name("Porto Alegre")
                .parentCategory(brazil)
                .build();
        categoryList.add(brazilPoa);
        return categoryList;
    }

}
