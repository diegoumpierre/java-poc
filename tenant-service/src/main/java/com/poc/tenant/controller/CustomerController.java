package com.poc.tenant.controller;

import com.poc.tenant.model.response.CustomerResponse;
import com.poc.tenant.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CustomerResponse>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(customerService.findByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(customerService.search(q));
    }
}
