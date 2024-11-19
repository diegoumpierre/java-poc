package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.Product;
import com.poc.domain.StateEnum;
import com.poc.domain.TaxSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductService {

    @Autowired
    FeeService feeService;

    public Product insert (String name, Double value, StateEnum stateEnum, Integer year){

        //try get the fee, if don't exist create one
        Fee fee = feeService.getFeeByStateAndYear(stateEnum, year);
        if (fee == null){
            fee = feeService.insert(stateEnum,year,0d);
        }

        Product product = new Product();
        product.setName(name);
        product.setValue(value);
        product.getFeeList().add(fee);

        TaxSystem.PRODUCT_LIST.add(product);
        return product;
    }

    public List<Product> getAll(){
        return TaxSystem.PRODUCT_LIST;
    }

    public void removeFeeFromProduct(Fee fee){
        TaxSystem.PRODUCT_LIST.stream().forEach(product -> product.getFeeList().remove(fee));
    }
}
