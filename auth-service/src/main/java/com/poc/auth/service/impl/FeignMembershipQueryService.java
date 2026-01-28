package com.poc.auth.service.impl;

import com.poc.auth.client.TenantClient;
import com.poc.auth.client.dto.MembershipDto;
import com.poc.auth.model.response.MembershipResponse;
import com.poc.auth.service.MembershipQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MembershipQueryService implementation that delegates to tenant-service via Feign.
 */
@Service("feignMembershipQueryService")
@RequiredArgsConstructor
@Slf4j
public class FeignMembershipQueryService implements MembershipQueryService {

    private final TenantClient tenantClient;

    @Override
    public List<MembershipResponse> findByUserId(UUID userId) {
        try {
            List<MembershipDto> dtos = tenantClient.getMembershipsByUserId(userId, userId.toString());
            return dtos.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get memberships from tenant-service for user {} - returning empty list (user will see currentTenantId=null and redirect to onboarding)", userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<MembershipResponse> findActiveByUserId(UUID userId) {
        try {
            List<MembershipDto> dtos = tenantClient.getMembershipsByUserId(userId, userId.toString());
            return dtos.stream()
                    .filter(MembershipDto::isActive)
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get active memberships from tenant-service for user {} - returning empty list (user will see currentTenantId=null and redirect to onboarding)", userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public MembershipResponse findById(UUID id) {
        try {
            MembershipDto dto = tenantClient.getMembershipById(id, id.toString());
            return convertToResponse(dto);
        } catch (Exception e) {
            log.error("Failed to get membership from tenant-service {}: {}", id, e.getMessage());
            return null;
        }
    }

    private MembershipResponse convertToResponse(MembershipDto dto) {
        MembershipResponse.MembershipResponseBuilder builder = MembershipResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .tenantId(dto.getTenantId())
                .status(dto.getStatus())
                .roleIds(dto.getRoleIds())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt());

        // Copy embedded tenant info if available (now LOCAL in tenant-service)
        if (dto.getTenant() != null) {
            builder.tenantName(dto.getTenant().getName())
                    .tenantSlug(dto.getTenant().getSlug())
                    .tenantType(dto.getTenant().getTenantType())
                    .tenantStatus(dto.getTenant().getStatus())
                    .tenantParentId(dto.getTenant().getParentTenantId());
        }

        return builder.build();
    }
}
