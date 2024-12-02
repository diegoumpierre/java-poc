package com.poc.account.service;

import com.poc.account.dao.CategoryDao;
import com.poc.account.model.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDao categoryDao = new CategoryDao();


    public List<Category> getAll(){
        return categoryDao.getAll();
    }

    public void insert(Category category){
        categoryDao.insert(category);
    }


}
