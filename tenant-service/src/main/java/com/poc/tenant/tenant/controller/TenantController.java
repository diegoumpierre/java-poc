package com.poc.tenant.tenant.controller;

import com.poc.tenant.model.request.TenantRequest;
import com.poc.tenant.model.response.TenantResponse;
import com.poc.tenant.security.TenantContext;
import com.poc.tenant.tenant.service.TenantService;
import com.poc.shared.security.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant management")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @Operation(summary = "List all tenants")
    public ResponseEntity<List<TenantResponse>> findAll() {
        return ResponseEntity.ok(tenantService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID")
    public ResponseEntity<TenantResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get tenant by slug")
    public ResponseEntity<TenantResponse> findBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(tenantService.findBySlug(slug));
    }

    @GetMapping("/search")
    @Operation(summary = "Search tenants by name")
    public ResponseEntity<List<TenantResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(tenantService.search(q));
    }

    @GetMapping("/{id}/children")
    @Operation(summary = "Get child tenants")
    public ResponseEntity<List<TenantResponse>> getChildren(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.findByParentId(id));
    }

    @PostMapping
    @Operation(summary = "Create new tenant")
    @RequiresPermission("TENANT_MANAGE")
    public ResponseEntity<TenantResponse> create(@Valid @RequestBody TenantRequest request) {
        return ResponseEntity.ok(tenantService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant")
    @RequiresPermission("TENANT_MANAGE")
    public ResponseEntity<TenantResponse> update(@PathVariable UUID id, @Valid @RequestBody TenantRequest request) {
        return ResponseEntity.ok(tenantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant (soft delete)")
    @RequiresPermission("TENANT_MANAGE")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID currentUser = TenantContext.getUserId();
        tenantService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
