package com.poc.tenant.tenant.controller;

import com.poc.tenant.model.request.CreateClientRequest;
import com.poc.tenant.model.request.CreatePartnerRequest;
import com.poc.tenant.model.request.SuspendTenantRequest;
import com.poc.tenant.model.response.TenantHierarchyStatsResponse;
import com.poc.tenant.model.response.TenantProvisioningResult;
import com.poc.tenant.model.response.TenantResponse;
import com.poc.tenant.model.response.TenantTreeNodeResponse;
import com.poc.tenant.tenant.service.TenantProvisioningService;
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
@RequestMapping("/api/tenants/hierarchy")
@RequiredArgsConstructor
@Tag(name = "Tenant Hierarchy", description = "Tenant hierarchy management for Platform Admins")
public class TenantHierarchyController {

    private final TenantService tenantService;
    private final TenantProvisioningService provisioningService;

    @GetMapping("/tree")
    @Operation(summary = "Get full tenant hierarchy tree")
    public ResponseEntity<TenantTreeNodeResponse> getHierarchyTree() {
        return ResponseEntity.ok(tenantService.getHierarchyTree());
    }

    @GetMapping("/stats")
    @Operation(summary = "Get hierarchy statistics")
    public ResponseEntity<TenantHierarchyStatsResponse> getHierarchyStats() {
        return ResponseEntity.ok(tenantService.getHierarchyStats());
    }

    @GetMapping("/children")
    @Operation(summary = "Get children of a tenant or filter by type")
    public ResponseEntity<List<TenantResponse>> getHierarchyChildren(
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(tenantService.getHierarchyChildren(parentId, type));
    }

    @PostMapping("/partner")
    @Operation(summary = "Create a new partner (reseller) tenant with full provisioning")
    @RequiresPermission("RESELLER_MANAGE")
    public ResponseEntity<TenantProvisioningResult> createPartner(@Valid @RequestBody CreatePartnerRequest request) {
        return ResponseEntity.ok(provisioningService.provisionPartner(request));
    }

    @PostMapping("/client")
    @Operation(summary = "Create a new client tenant with full provisioning")
    @RequiresPermission("RESELLER_MANAGE")
    public ResponseEntity<TenantProvisioningResult> createClient(
            @Valid @RequestBody CreateClientRequest request,
            @RequestParam(required = false) UUID parentId) {
        return ResponseEntity.ok(provisioningService.provisionClient(request, parentId));
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend a tenant")
    @RequiresPermission("RESELLER_MANAGE")
    public ResponseEntity<TenantResponse> suspendTenant(
            @PathVariable UUID id,
            @Valid @RequestBody SuspendTenantRequest request) {
        return ResponseEntity.ok(tenantService.suspendTenant(id, request));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate a suspended tenant")
    @RequiresPermission("RESELLER_MANAGE")
    public ResponseEntity<TenantResponse> activateTenant(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.activateTenant(id));
    }
}
