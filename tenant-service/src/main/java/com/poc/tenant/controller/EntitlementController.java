package com.poc.tenant.controller;

import com.poc.tenant.model.request.CreateEntitlementRequest;
import com.poc.tenant.model.response.EntitlementResponse;
import com.poc.tenant.service.EntitlementService;
import com.poc.shared.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants/entitlements")
@RequiredArgsConstructor
public class EntitlementController {

    private final EntitlementService entitlementService;

    @GetMapping
    public ResponseEntity<List<EntitlementResponse>> findByCurrentTenant() {
        return ResponseEntity.ok(entitlementService.findByCurrentTenant());
    }

    @GetMapping("/current")
    public ResponseEntity<List<EntitlementResponse>> getMyEntitlements() {
        return ResponseEntity.ok(entitlementService.findByCurrentTenant());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<EntitlementResponse>> findByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(entitlementService.findByCurrentTenantAndProduct(productId));
    }

    @GetMapping("/check/{featureCode}")
    public ResponseEntity<Map<String, Object>> checkEntitlement(@PathVariable String featureCode) {
        boolean hasAccess = entitlementService.hasEntitlement(featureCode);
        return ResponseEntity.ok(Map.of(
                "featureCode", featureCode,
                "hasAccess", hasAccess
        ));
    }

    @GetMapping("/check/{featureCode}/tenant/{tenantId}")
    public ResponseEntity<Map<String, Object>> checkEntitlementForTenant(
            @PathVariable String featureCode, @PathVariable UUID tenantId) {
        boolean hasAccess = entitlementService.hasEntitlement(tenantId, featureCode);
        return ResponseEntity.ok(Map.of(
                "featureCode", featureCode,
                "tenantId", tenantId,
                "hasAccess", hasAccess
        ));
    }

    @GetMapping("/feature/{featureCode}")
    public ResponseEntity<EntitlementResponse> getEntitlement(@PathVariable String featureCode) {
        return ResponseEntity.ok(entitlementService.getEntitlement(featureCode));
    }

    @PostMapping
    @RequiresPermission("BILLING_MANAGE")
    public ResponseEntity<EntitlementResponse> createEntitlement(@Valid @RequestBody CreateEntitlementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(entitlementService.createEntitlement(request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("BILLING_MANAGE")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        entitlementService.revokeEntitlement(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-source")
    @RequiresPermission("BILLING_MANAGE")
    public ResponseEntity<Void> revokeBySource(@RequestParam UUID tenantId, @RequestParam String source) {
        entitlementService.revokeByTenantIdAndSource(tenantId, source);
        return ResponseEntity.noContent().build();
    }
}
