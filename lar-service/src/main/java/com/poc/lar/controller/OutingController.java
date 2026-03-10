package com.poc.lar.controller;

import com.poc.lar.dto.OutingApprovalRequest;
import com.poc.lar.dto.OutingDTO;
import com.poc.lar.dto.OutingRejectionRequest;
import com.poc.lar.dto.OutingRequestRequest;
import com.poc.lar.service.OutingService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/outings")
@RequiredArgsConstructor
public class OutingController {

    private final OutingService service;

    @GetMapping
    public ResponseEntity<List<OutingDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutingDTO> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<OutingDTO> create(@Valid @RequestBody OutingRequestRequest request) {
        OutingDTO created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<OutingDTO> update(@PathVariable UUID id, @Valid @RequestBody OutingRequestRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping("/{id}/approve")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<OutingDTO> approve(@PathVariable UUID id, @RequestBody OutingApprovalRequest request) {
        return ResponseEntity.ok(service.approve(id, request));
    }

    @PostMapping("/{id}/reject")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<OutingDTO> reject(@PathVariable UUID id, @Valid @RequestBody OutingRejectionRequest request) {
        return ResponseEntity.ok(service.reject(id, request));
    }

    @PostMapping("/{id}/depart")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<OutingDTO> depart(@PathVariable UUID id) {
        return ResponseEntity.ok(service.depart(id));
    }

    @PostMapping("/{id}/return")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<OutingDTO> returnHome(@PathVariable UUID id) {
        return ResponseEntity.ok(service.returnHome(id));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OutingDTO>> findPending() {
        return ResponseEntity.ok(service.findPending());
    }

    @GetMapping("/active")
    public ResponseEntity<List<OutingDTO>> findActive() {
        return ResponseEntity.ok(service.findActive());
    }

    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<OutingDTO>> findByMember(@PathVariable UUID memberId) {
        return ResponseEntity.ok(service.findByMember(memberId));
    }
}
