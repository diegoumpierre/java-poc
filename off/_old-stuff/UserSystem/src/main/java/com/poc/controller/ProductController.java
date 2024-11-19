package com.poc.controller;

import com.poc.domain.Fee;
import com.poc.domain.Product;
import com.poc.service.FeeService;
import com.poc.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Tax System - Product")
@RestController
@RequestMapping(value="/product")
@Slf4j
public class ProductController {
    @Autowired
    ProductService productService;

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all Product")
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Insert Product")
    public  ResponseEntity<Fee> insert(@RequestBody Product product) {
        return ResponseEntity.ok(null);
//        return ResponseEntity.ok(productService.insert(product.getName(),product.getValue(),product.g);
    }


}
