package com.poc.chat.controller;

import com.poc.chat.dto.chat.TenantSettingsDTO;
import com.poc.chat.service.TenantSettingsService;
import com.poc.shared.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/admin/settings")
@RequiredArgsConstructor
public class TenantSettingsController {

    private final TenantSettingsService tenantSettingsService;

    @GetMapping
    public ResponseEntity<TenantSettingsDTO> getSettings() {
        return ResponseEntity.ok(tenantSettingsService.getSettings());
    }

    @PutMapping
    @RequiresPermission("CHAT_MANAGE")
    public ResponseEntity<TenantSettingsDTO> updateSettings(@RequestBody TenantSettingsDTO request) {
        return ResponseEntity.ok(tenantSettingsService.updateSettings(request));
    }
}
