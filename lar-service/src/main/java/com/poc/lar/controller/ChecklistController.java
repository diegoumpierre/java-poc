package com.poc.lar.controller;

import com.poc.lar.dto.ChecklistResponseDTO;
import com.poc.lar.dto.ChecklistSubmitRequest;
import com.poc.lar.dto.ChecklistTemplateDTO;
import com.poc.lar.dto.ChecklistTemplateRequest;
import com.poc.lar.service.ChecklistService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lar/checklists")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService service;

    @GetMapping
    public ResponseEntity<List<ChecklistTemplateDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ChecklistTemplateDTO> create(@Valid @RequestBody ChecklistTemplateRequest request) {
        ChecklistTemplateDTO created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ChecklistTemplateDTO> update(@PathVariable UUID id, @Valid @RequestBody ChecklistTemplateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/respond")
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<ChecklistResponseDTO> submitResponse(
            @PathVariable UUID id,
            @Valid @RequestBody ChecklistSubmitRequest request,
            @RequestParam(required = false) UUID outingId) {
        ChecklistResponseDTO response = service.submitResponse(id, outingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/responses/{outingId}")
    public ResponseEntity<List<ChecklistResponseDTO>> findResponseByOuting(@PathVariable UUID outingId) {
        return ResponseEntity.ok(service.findResponseByOuting(outingId));
    }
}
