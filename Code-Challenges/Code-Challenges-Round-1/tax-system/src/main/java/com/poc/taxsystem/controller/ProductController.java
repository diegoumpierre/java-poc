package com.poc.taxsystem.controller;

import com.poc.taxsystem.domain.Product;
import com.poc.taxsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/product"})
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(this.productService.getAll());
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Product product) {
        return (ResponseEntity<Void>) ResponseEntity.ok();
    }
}