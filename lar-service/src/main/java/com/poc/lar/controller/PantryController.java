package com.poc.lar.controller;

import com.poc.lar.dto.PantryItemDTO;
import com.poc.lar.dto.PantryItemRequest;
import com.poc.lar.dto.ShoppingListDTO;
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
@RequestMapping("/api/lar/pantry")
@RequiredArgsConstructor
public class PantryController {

    private final ShoppingService shoppingService;

    @GetMapping
    public ResponseEntity<List<PantryItemDTO>> findAll() {
        return ResponseEntity.ok(shoppingService.findAllPantry());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<PantryItemDTO> create(@Valid @RequestBody PantryItemRequest request) {
        PantryItemDTO created = shoppingService.createPantryItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<PantryItemDTO> update(@PathVariable UUID id, @Valid @RequestBody PantryItemRequest request) {
        return ResponseEntity.ok(shoppingService.updatePantryItem(id, request));
    }

    @GetMapping("/low")
    public ResponseEntity<List<PantryItemDTO>> findLow() {
        return ResponseEntity.ok(shoppingService.findLowPantry());
    }

    @PostMapping("/generate-list")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ShoppingListDTO> generateList() {
        ShoppingListDTO generated = shoppingService.generateListFromPantry();
        return ResponseEntity.status(HttpStatus.CREATED).body(generated);
    }
}
