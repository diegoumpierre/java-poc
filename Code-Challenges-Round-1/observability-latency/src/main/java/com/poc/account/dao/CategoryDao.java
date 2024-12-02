package com.poc.account.dao;

import com.poc.account.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    private static final List<Category> categoryList = new ArrayList<>();


    public List<Category> getAll(){
        return categoryList;
    }


    public void insert(Category category){
        categoryList.add(category);
    }

}
