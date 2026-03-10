package com.poc.lar.controller;

import com.poc.lar.dto.ShoppingItemDTO;
import com.poc.lar.dto.ShoppingItemRequest;
import com.poc.lar.dto.ShoppingListDTO;
import com.poc.lar.dto.ShoppingListRequest;
import com.poc.lar.service.ShoppingService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/shopping")
@RequiredArgsConstructor
public class ShoppingController {

    private final ShoppingService service;

    // --- Lists ---

    @GetMapping
    public ResponseEntity<List<ShoppingListDTO>> findAll() {
        return ResponseEntity.ok(service.findAllLists());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ShoppingListDTO> create(@Valid @RequestBody ShoppingListRequest request) {
        ShoppingListDTO created = service.createList(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ShoppingListDTO> update(@PathVariable UUID id, @Valid @RequestBody ShoppingListRequest request) {
        return ResponseEntity.ok(service.updateList(id, request));
    }

    @PostMapping("/{id}/complete")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ShoppingListDTO> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(service.completeList(id));
    }

    // --- Items ---

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ShoppingItemDTO>> findItems(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findItemsByList(id));
    }

    @PostMapping("/{id}/items")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ShoppingItemDTO> addItem(@PathVariable UUID id, @Valid @RequestBody ShoppingItemRequest request) {
        ShoppingItemDTO created = service.addItem(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/items/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ShoppingItemDTO> updateItem(@PathVariable UUID id, @Valid @RequestBody ShoppingItemRequest request) {
        return ResponseEntity.ok(service.updateItem(id, request));
    }

    @PostMapping("/items/{id}/check")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ShoppingItemDTO> checkItem(@PathVariable UUID id) {
        return ResponseEntity.ok(service.checkItem(id));
    }
}
