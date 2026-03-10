package com.poc.lar.controller;

import com.poc.lar.dto.DashboardDTO;
import com.poc.lar.dto.FamilyMemberDTO;
import com.poc.lar.dto.FamilyMemberRequest;
import com.poc.lar.service.FamilyMemberService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/members")
@RequiredArgsConstructor
public class FamilyMemberController {

    private final FamilyMemberService service;

    @GetMapping
    public ResponseEntity<List<FamilyMemberDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyMemberDTO> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<FamilyMemberDTO> create(@Valid @RequestBody FamilyMemberRequest request) {
        FamilyMemberDTO created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<FamilyMemberDTO> update(@PathVariable UUID id, @Valid @RequestBody FamilyMemberRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getDashboard(id));
    }
}
