package com.poc.lar.controller;

import com.poc.lar.dto.ContactDTO;
import com.poc.lar.dto.ContactRequest;
import com.poc.lar.service.ContactService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService service;

    @GetMapping
    public ResponseEntity<List<ContactDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<List<ContactDTO>> findByMemberId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findByMemberId(id));
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ContactDTO> create(@Valid @RequestBody ContactRequest request) {
        ContactDTO created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ContactDTO> update(@PathVariable UUID id, @Valid @RequestBody ContactRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trusted")
    public ResponseEntity<List<ContactDTO>> findTrusted() {
        return ResponseEntity.ok(service.findTrusted());
    }
}
