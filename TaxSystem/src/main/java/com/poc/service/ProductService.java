package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.Product;
import com.poc.domain.TaxSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductService {

    @Autowired
    FeeService feeService;


    public Product insert(String name, Double value, Fee fee){
        List<Fee> feeList = new ArrayList<>();
        feeList.add(fee);
        Product product = Product.builder()
                .name(name)
                .value(value)
                .feeList(feeList)
                .build();
        TaxSystem.PRODUCT_LIST.add(product);
        return product;
    }

    public void remove(Product product){
        List<Product> productListToRemove = new ArrayList<>();
        TaxSystem.PRODUCT_LIST.stream().forEach(product1 -> {
            if(product1.getName().equals(product.getName()))
                productListToRemove.add(product1);
        });
        TaxSystem.PRODUCT_LIST.removeAll(productListToRemove);
    }

    public List<Product> getAll(){
        return new ArrayList<>(TaxSystem.PRODUCT_LIST);
    }

        public void removeFeeFromProduct(Fee fee){
        TaxSystem.PRODUCT_LIST.forEach(product -> product.getFeeList().remove(fee));
    }

//    public Product insert (String name, Double value, StateEnum stateEnum, Integer year){
//
//        //try get the fee, if don't exist create one
//        Fee fee = feeService.getFeeByStateAndYear(stateEnum, year);
//        if (fee == null){
//            fee = feeService.insert(stateEnum,year,0d);
//        }
//
//        Product product = new Product();
//        product.setName(name);
//        product.setValue(value);
//        product.getFeeList().add(fee);
//
//        TaxSystem.PRODUCT_LIST.add(product);
//        return product;
//    }





}
