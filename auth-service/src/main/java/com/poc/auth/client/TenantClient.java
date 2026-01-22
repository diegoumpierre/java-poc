package com.poc.auth.client;

import com.poc.auth.client.dto.EntitlementResponse;
import com.poc.auth.client.dto.MembershipDto;
import com.poc.auth.client.dto.TenantDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for tenant-service.
 */
@FeignClient(name = "tenant-service", url = "${app.tenant.url:http://localhost:8094}")
public interface TenantClient {

    // Entitlement endpoints
    @GetMapping("/api/tenants/entitlements")
    List<EntitlementResponse> getEntitlementsByTenant(
            @RequestHeader("X-Tenant-Id") String tenantIdHeader
    );


    // Tenant endpoints
    @GetMapping("/api/tenants")
    List<TenantDto> getAllTenants(@RequestHeader("X-User-Id") String userId);

    @GetMapping("/api/tenants/{id}")
    TenantDto getTenantById(
            @PathVariable("id") UUID id,
            @RequestHeader("X-User-Id") String userId
    );

    @GetMapping("/api/tenants/slug/{slug}")
    TenantDto getTenantBySlug(
            @PathVariable("slug") String slug,
            @RequestHeader("X-User-Id") String userId
    );

    @GetMapping("/api/tenants/search")
    List<TenantDto> searchTenants(
            @RequestParam("q") String query,
            @RequestHeader("X-User-Id") String userId
    );

    @GetMapping("/api/tenants/{id}/children")
    List<TenantDto> getChildTenants(
            @PathVariable("id") UUID parentId,
            @RequestHeader("X-User-Id") String userId
    );

    // =========================================================================
    // Membership endpoints (migrated from user-service)
    // =========================================================================

    @GetMapping("/api/tenants/internal/memberships/user/{userId}")
    List<MembershipDto> getMembershipsByUserId(
            @PathVariable("userId") UUID userId,
            @RequestHeader("X-User-Id") String userIdHeader
    );

    @GetMapping("/api/tenants/internal/memberships/{id}")
    MembershipDto getMembershipById(
            @PathVariable("id") UUID id,
            @RequestHeader("X-User-Id") String userId
    );
}
