package com.poc.tenant.menu.controller;

import com.poc.shared.security.SecurityContext;
import com.poc.tenant.menu.service.CachedMenuService;
import com.poc.tenant.menu.service.MenuService;
import com.poc.tenant.model.response.MenuItemResponse;
import com.poc.tenant.security.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/menus")
@RequiredArgsConstructor
@Tag(name = "Menus", description = "Navigation menu management")
public class MenuController {

    private final MenuService menuService;
    private final CachedMenuService cachedMenuService;

    @GetMapping
    @Operation(summary = "Get menus for current tenant (filtered by entitlements, roles, permissions)")
    public ResponseEntity<List<MenuItemResponse>> getMenus() {
        UUID tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(menuService.getMenusForTenant(
                tenantId,
                TenantContext.getUserRoles(),
                SecurityContext.getPermissions()));
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get menus for specific tenant (admin, unfiltered)")
    public ResponseEntity<List<MenuItemResponse>> getMenusForTenant(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(menuService.getUnfilteredMenusForTenant(tenantId));
    }

    @GetMapping("/platform")
    @Operation(summary = "Get platform-level menus")
    public ResponseEntity<List<MenuItemResponse>> getPlatformMenus() {
        return ResponseEntity.ok(menuService.getPlatformMenus());
    }

    @PostMapping("/cache/evict")
    @Operation(summary = "Evict menu cache (admin)")
    public ResponseEntity<Void> evictCache() {
        cachedMenuService.evictCache();
        return ResponseEntity.noContent().build();
    }
}
