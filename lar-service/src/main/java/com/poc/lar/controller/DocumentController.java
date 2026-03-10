package com.poc.lar.controller;

import com.poc.lar.dto.DocumentDTO;
import com.poc.lar.dto.DocumentRequest;
import com.poc.lar.service.DocumentService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @GetMapping
    public ResponseEntity<List<DocumentDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<List<DocumentDTO>> findByMember(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findByMember(id));
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<DocumentDTO> create(@Valid @RequestBody DocumentRequest request) {
        DocumentDTO created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<DocumentDTO> update(@PathVariable UUID id, @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<DocumentDTO>> findExpiring() {
        return ResponseEntity.ok(service.findExpiring());
    }
}
