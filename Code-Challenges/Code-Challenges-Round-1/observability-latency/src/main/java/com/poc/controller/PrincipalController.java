package com.poc.controller;
import com.poc.account.model.Account;
import com.poc.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/products")
public class PrincipalController {


    @Autowired
    private AccountService productService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Account>> findAll(){
        List<Account> allProducts = productService.getAll();
        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

//    @GetMapping(path="/{id}", produces = "application/json")
//    public ResponseEntity<Product> findById(@PathVariable("id") Long id){
//        Optional<Product> product = productService.findById(id);
//        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> ResponseEntity.notFound().build());
//    }


}
