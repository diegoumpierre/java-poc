package com.poc.auth.service;

import com.poc.auth.client.dto.EntitlementResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for querying entitlements.
 *
 * Implementations:
 * - FeignEntitlementQueryService: Calls tenant-service via Feign
 * - CachedEntitlementQueryService: Adds Redis caching layer
 */
public interface EntitlementQueryService {

    /**
     * Find all active entitlements for a tenant
     */
    List<EntitlementResponse> findByTenantId(UUID tenantId);
}
