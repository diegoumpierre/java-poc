package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.Product;
import com.poc.domain.TaxSystem;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @InjectMocks
    FeeService feeService;

    @Test
    void insertProductShouldBeSuccess() {
        Fee fee = DataTest.gimmeFee();
        Product result = productService.insert("product1",102d,fee);
        assertEquals("product1", result.getName());
        assertEquals(102d, result.getValue());
        assertThat(TaxSystem.PRODUCT_LIST, hasItem(result));
    }

    @Test
    void removeProductShouldBeSuccess(){
        Fee fee = DataTest.gimmeFee();
        List<Fee> feeList = new ArrayList<>();
        feeList.add(fee);
        Product productToRemove = Product.builder()
                .feeList(feeList)
                .value(102d)
                .name("product1")
                .build();
        TaxSystem.PRODUCT_LIST.add(productToRemove);
        productService.remove(productToRemove);
        assertThat(TaxSystem.PRODUCT_LIST, not(hasItem(productToRemove)));
    }



}