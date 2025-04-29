package com.poc.antlrcalc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class AntlrCalcController {
    @GetMapping(value = "/")
    public ResponseEntity<String> findById(){
        return ResponseEntity.ok("welcome");
    }

}
