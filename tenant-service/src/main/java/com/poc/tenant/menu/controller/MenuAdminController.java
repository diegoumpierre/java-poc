package com.poc.tenant.menu.controller;

import com.poc.shared.security.RequiresPermission;
import com.poc.tenant.menu.service.MenuService;
import com.poc.tenant.model.response.MenuItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/admin/menus")
@RequiredArgsConstructor
@Tag(name = "Menu Admin", description = "Platform menu CRUD management")
public class MenuAdminController {

    private final MenuService menuService;

    @GetMapping
    @Operation(summary = "Get all platform menus for admin (including non-visible)")
    public ResponseEntity<List<MenuItemResponse>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenusForAdmin());
    }

    @PostMapping
    @Operation(summary = "Create a new menu item")
    @RequiresPermission("MENU_MANAGE")
    public ResponseEntity<MenuItemResponse> createMenuItem(@RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuService.createMenuItem(
                request.parentId(),
                request.menuKey(),
                request.label(),
                request.icon(),
                request.effectiveRoute(),
                request.url(),
                request.target(),
                request.category(),
                request.featureCodes(),
                request.roles(),
                request.permissions(),
                request.orderIndex(),
                request.visible(),
                request.badge(),
                request.effectiveBadgeClass(),
                request.separator()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a menu item")
    @RequiresPermission("MENU_MANAGE")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable UUID id,
            @RequestBody MenuItemRequest request) {
        MenuItemResponse response = menuService.updateMenuItem(
                id,
                request.parentId(),
                request.menuKey(),
                request.label(),
                request.icon(),
                request.effectiveRoute(),
                request.url(),
                request.target(),
                request.category(),
                request.featureCodes(),
                request.roles(),
                request.permissions(),
                request.orderIndex(),
                request.visible(),
                request.badge(),
                request.effectiveBadgeClass(),
                request.separator()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a menu item and its children")
    @RequiresPermission("MENU_MANAGE")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/move")
    @Operation(summary = "Move a menu item to a new parent and/or position")
    @RequiresPermission("MENU_MANAGE")
    public ResponseEntity<MenuItemResponse> moveMenuItem(
            @PathVariable UUID id,
            @RequestBody MoveMenuRequest request) {
        MenuItemResponse response = menuService.moveMenuItem(id, request.parentId(), request.orderIndex());
        return ResponseEntity.ok(response);
    }

    public record MenuItemRequest(
            UUID parentId,
            String menuKey,
            String label,
            String icon,
            String route,
            String to,
            String url,
            String target,
            String category,
            List<String> featureCodes,
            List<String> roles,
            List<String> permissions,
            Integer orderIndex,
            Boolean visible,
            String badge,
            String badgeClass,
            String badgeClassName,
            Boolean separator
    ) {
        public String effectiveRoute() {
            return route != null ? route : to;
        }

        public String effectiveBadgeClass() {
            return badgeClass != null ? badgeClass : badgeClassName;
        }
    }

    public record MoveMenuRequest(
            UUID parentId,
            Integer orderIndex
    ) {}
}
