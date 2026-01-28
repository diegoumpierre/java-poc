package com.poc.auth.service.impl;

import com.poc.auth.client.TenantClient;
import com.poc.auth.client.dto.EntitlementResponse;
import com.poc.auth.service.EntitlementQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * EntitlementQueryService implementation that delegates to tenant-service via Feign.
 */
@Service("feignEntitlementQueryService")
@RequiredArgsConstructor
@Slf4j
public class FeignEntitlementQueryService implements EntitlementQueryService {

    private final TenantClient tenantClient;

    @Override
    public List<EntitlementResponse> findByTenantId(UUID tenantId) {
        try {
            return tenantClient.getEntitlementsByTenant(tenantId.toString());
        } catch (Exception e) {
            log.error("Failed to get entitlements from tenant-service for tenant {}: {}", tenantId, e.getMessage());
            return Collections.emptyList();
        }
    }
}
