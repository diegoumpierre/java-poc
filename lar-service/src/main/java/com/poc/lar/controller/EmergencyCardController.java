package com.poc.lar.controller;

import com.poc.lar.dto.EmergencyCardDTO;
import com.poc.lar.dto.EmergencyCardRequest;
import com.poc.lar.service.DocumentService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/lar/emergency")
@RequiredArgsConstructor
public class EmergencyCardController {

    private final DocumentService documentService;

    @GetMapping("/{memberId}")
    public ResponseEntity<EmergencyCardDTO> findByMember(@PathVariable UUID memberId) {
        return documentService.findEmergencyByMember(memberId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequiresPermission("LAR_MANAGE")
    public ResponseEntity<EmergencyCardDTO> createOrUpdate(@Valid @RequestBody EmergencyCardRequest request) {
        return ResponseEntity.ok(documentService.createOrUpdateEmergency(request));
    }

    @GetMapping("/public/{token}")
    public ResponseEntity<EmergencyCardDTO> findByPublicToken(@PathVariable UUID token) {
        return documentService.findByPublicToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
