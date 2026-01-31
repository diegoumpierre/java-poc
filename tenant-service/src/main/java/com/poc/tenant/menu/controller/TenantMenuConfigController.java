package com.poc.tenant.menu.controller;

import com.poc.tenant.menu.domain.TenantMenuConfig;
import com.poc.tenant.menu.service.TenantMenuConfigService;
import com.poc.tenant.model.response.TenantMenuConfigResponse;
import com.poc.tenant.security.TenantContext;
import com.poc.shared.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/admin/menu-config")
@RequiredArgsConstructor
@Tag(name = "Tenant Menu Config", description = "Tenant-specific menu configuration")
public class TenantMenuConfigController {

    private final TenantMenuConfigService tenantMenuConfigService;

    @GetMapping("/effective-disabled")
    @Operation(summary = "Get effective disabled menu IDs including parent tenant restrictions")
    public ResponseEntity<List<String>> getEffectiveDisabledMenus() {
        UUID tenantId = TenantContext.getTenantId();
        List<String> disabledMenuIds = tenantMenuConfigService.getEffectiveDisabledMenuIds(tenantId);
        return ResponseEntity.ok(disabledMenuIds);
    }

    @GetMapping
    @Operation(summary = "Get menu configurations for current tenant")
    public ResponseEntity<List<TenantMenuConfigResponse>> getMenuConfigs() {
        UUID tenantId = TenantContext.getTenantId();
        List<TenantMenuConfigResponse> configs = tenantMenuConfigService.getMenuConfigs(tenantId);
        return ResponseEntity.ok(configs);
    }

    @PutMapping
    @Operation(summary = "Save menu configurations for current tenant")
    @RequiresPermission("MENU_MANAGE")
    public ResponseEntity<List<TenantMenuConfigResponse>> saveMenuConfigs(
            @RequestBody List<TenantMenuConfigRequest> requests) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();
        List<TenantMenuConfigResponse> saved = tenantMenuConfigService.saveMenuConfigs(tenantId, requests, userId);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{menuId}")
    @Operation(summary = "Toggle a specific menu item")
    @RequiresPermission("MENU_MANAGE")
    public ResponseEntity<TenantMenuConfigResponse> toggleMenu(
            @PathVariable String menuId,
            @RequestBody ToggleMenuRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();
        TenantMenuConfigResponse response = tenantMenuConfigService.toggleMenu(tenantId, menuId, request.enabled(), userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/copy-from-parent")
    @Operation(summary = "Copy menu configuration from parent tenant")
    @RequiresPermission("MENU_MANAGE")
    public ResponseEntity<Void> copyFromParent() {
        UUID tenantId = TenantContext.getTenantId();
        UUID userId = TenantContext.getUserId();
        tenantMenuConfigService.copyFromParent(tenantId, userId);
        return ResponseEntity.ok().build();
    }

    // Request DTOs
    public record TenantMenuConfigRequest(
            String menuId,
            Boolean enabled
    ) {}

    public record ToggleMenuRequest(
            Boolean enabled
    ) {}
}
