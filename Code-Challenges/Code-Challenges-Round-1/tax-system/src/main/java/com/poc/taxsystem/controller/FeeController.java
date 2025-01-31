package com.poc.taxsystem.controller;

import com.poc.taxsystem.domain.Fee;
import com.poc.taxsystem.dto.FeeDTO;
import com.poc.taxsystem.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/fee"})
public class FeeController {
    @Autowired
    FeeService feeService;

    @PostMapping
    public ResponseEntity<Fee> insert(@RequestBody FeeDTO feeDto) {
        Fee fee = this.feeService.insert(feeDto.getState(), feeDto.getYear(), feeDto.getValue());
        return ResponseEntity.ok(fee);
    }

    @DeleteMapping
    public ResponseEntity<String> remove(@RequestBody FeeDTO feeDto) {
            this.feeService.remove(feeDto.getState(), feeDto.getYear());
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    public ResponseEntity<List<Fee>> getAll() {
        return ResponseEntity.ok(this.feeService.getAll());
    }

}