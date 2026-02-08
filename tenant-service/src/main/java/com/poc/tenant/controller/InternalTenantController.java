package com.poc.tenant.controller;

import com.poc.shared.tenant.TenantContext;
import com.poc.tenant.model.request.BulkEntitlementRequest;
import com.poc.tenant.model.request.TenantRequest;
import com.poc.tenant.model.response.EntitlementResponse;
import com.poc.tenant.model.response.TenantResponse;
import com.poc.tenant.service.EntitlementService;
import com.poc.tenant.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Internal endpoints for service-to-service provisioning.
 * No permission checks - accessible only via internal network.
 */
@RestController
@RequestMapping("/api/tenants/internal")
@RequiredArgsConstructor
@Tag(name = "Internal Tenant", description = "Internal endpoints for provisioning")
@Slf4j
public class InternalTenantController {

    private final TenantService tenantService;
    private final EntitlementService entitlementService;

    @PostMapping("/provision")
    @Operation(summary = "Create tenant (no permission check, for billing-service)")
    public ResponseEntity<TenantResponse> provisionTenant(@Valid @RequestBody TenantRequest request) {
        log.info("Internal tenant provisioning: name={}", request.getName());
        return ResponseEntity.ok(tenantService.create(request));
    }

    @PostMapping("/entitlements/provision")
    @Operation(summary = "Bulk provision entitlements for a tenant")
    public ResponseEntity<List<EntitlementResponse>> provisionEntitlements(
            @Valid @RequestBody BulkEntitlementRequest request) {
        log.info("Provisioning {} entitlements for tenant {}",
                request.getFeatures().size(), request.getTenantId());

        // Set TenantContext for internal service-to-service calls (no X-Tenant-Id header)
        TenantContext.setCurrentTenant(request.getTenantId());
        try {
            List<EntitlementResponse> results = new ArrayList<>();
            for (BulkEntitlementRequest.FeatureEntitlement feature : request.getFeatures()) {
                EntitlementResponse response = entitlementService.grantEntitlement(
                        request.getTenantId(),
                        request.getProductId(),
                        feature.getCode(),
                        "subscription",
                        feature.getLimitValue(),
                        null
                );
                results.add(response);
            }

            log.info("Provisioned {} entitlements for tenant {}", results.size(), request.getTenantId());
            return ResponseEntity.ok(results);
        } finally {
            TenantContext.clear();
        }
    }
}
