package com.poc.tenant.controller;

import com.poc.tenant.model.request.SupplierRequest;
import com.poc.tenant.model.response.SupplierResponse;
import com.poc.tenant.service.SupplierService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierResponse>> findAll() {
        return ResponseEntity.ok(supplierService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplierService.findById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SupplierResponse>> findByCategory(@PathVariable String category) {
        return ResponseEntity.ok(supplierService.findByCategory(category));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplierResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(supplierService.search(q));
    }

    @PostMapping
    @RequiresPermission("OS_MANAGE")
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresPermission("OS_MANAGE")
    public ResponseEntity<SupplierResponse> update(@PathVariable UUID id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("OS_MANAGE")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
