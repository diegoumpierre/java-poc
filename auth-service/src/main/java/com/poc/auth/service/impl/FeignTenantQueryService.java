package com.poc.auth.service.impl;

import com.poc.auth.client.TenantClient;
import com.poc.auth.client.dto.TenantDto;
import com.poc.auth.model.response.TenantResponse;
import com.poc.auth.service.TenantQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * TenantQueryService implementation that delegates to organization-service via Feign.
 */
@Service("feignTenantQueryService")
@RequiredArgsConstructor
@Slf4j
public class FeignTenantQueryService implements TenantQueryService {

    private final TenantClient tenantClient;

    @Override
    public List<TenantResponse> findAll() {
        try {
            List<TenantDto> dtos = tenantClient.getAllTenants("system");
            return dtos.stream()
                    .map(this::convertToResponse)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get all tenants from organization-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public TenantResponse findById(UUID id) {
        try {
            TenantDto dto = tenantClient.getTenantById(id, id.toString());
            return convertToResponse(dto);
        } catch (Exception e) {
            log.error("Failed to get tenant from organization-service {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public TenantResponse findBySlug(String slug) {
        try {
            TenantDto dto = tenantClient.getTenantBySlug(slug, "system");
            return convertToResponse(dto);
        } catch (Exception e) {
            log.error("Failed to get tenant by slug from organization-service {}: {}", slug, e.getMessage());
            return null;
        }
    }

    @Override
    public List<TenantResponse> search(String query) {
        try {
            List<TenantDto> dtos = tenantClient.searchTenants(query, "system");
            return dtos.stream()
                    .map(this::convertToResponse)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search tenants from organization-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<TenantResponse> findByParentId(UUID parentId) {
        try {
            List<TenantDto> dtos = tenantClient.getChildTenants(parentId, "system");
            return dtos.stream()
                    .map(this::convertToResponse)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get child tenants from organization-service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private TenantResponse convertToResponse(TenantDto dto) {
        return TenantResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .tenantType(dto.getTenantType())
                .status(dto.getStatus())
                .subscriptionStatus(dto.getSubscriptionStatus())
                .parentTenantId(dto.getParentTenantId())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
