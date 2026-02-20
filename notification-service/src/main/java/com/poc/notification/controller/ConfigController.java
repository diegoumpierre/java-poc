package com.poc.notification.controller;

import com.poc.notification.domain.TenantConfig;
import com.poc.notification.dto.TenantConfigRequest;
import com.poc.notification.service.DirectEmailService;
import com.poc.notification.service.TenantConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification/config")
@RequiredArgsConstructor
@Tag(name = "Config", description = "Tenant email configuration")
public class ConfigController {

    private final TenantConfigService tenantConfigService;
    private final DirectEmailService directEmailService;

    @PostMapping
    @Operation(summary = "Create or update email config")
    public ResponseEntity<TenantConfig> createOrUpdate(@Valid @RequestBody TenantConfigRequest request) {
        return ResponseEntity.ok(tenantConfigService.createOrUpdate(request));
    }

    @GetMapping
    @Operation(summary = "Get all configs for current tenant")
    public ResponseEntity<List<TenantConfig>> getConfigs() {
        return ResponseEntity.ok(tenantConfigService.getConfigs());
    }

    @GetMapping("/status")
    @Operation(summary = "Test SMTP/IMAP connections")
    public ResponseEntity<Map<String, Object>> testConnection(
            @RequestParam(defaultValue = "ATENDIMENTO") String configType) {
        return ResponseEntity.ok(tenantConfigService.testConnection(configType));
    }

    @PostMapping("/test-send")
    @Operation(summary = "Send a test email")
    public ResponseEntity<Map<String, String>> testSend(@RequestParam String to) {
        directEmailService.sendTestEmail(to);
        return ResponseEntity.ok(Map.of("status", "sent", "to", to));
    }
}
