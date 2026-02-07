package com.poc.tenant.service;

import com.poc.shared.tenant.TenantAware;
import com.poc.shared.tenant.TenantContext;
import com.poc.tenant.domain.Entitlement;
import com.poc.tenant.event.EntitlementEventPublisher;
import com.poc.tenant.exception.ResourceNotFoundException;
import com.poc.tenant.model.request.CreateEntitlementRequest;
import com.poc.tenant.model.response.EntitlementResponse;
import com.poc.tenant.repository.EntitlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@TenantAware
public class EntitlementService {

    private final EntitlementRepository entitlementRepository;

    @Autowired(required = false)
    private EntitlementEventPublisher eventPublisher;

    public List<EntitlementResponse> findByCurrentTenant() {
        UUID tenantId = TenantContext.getCurrentTenant();
        return entitlementRepository.findActiveByTenantId(tenantId).stream()
                .map(EntitlementResponse::from)
                .collect(Collectors.toList());
    }

    public List<EntitlementResponse> findByCurrentTenantAndProduct(UUID productId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return entitlementRepository.findActiveByTenantIdAndProductId(tenantId, productId).stream()
                .map(EntitlementResponse::from)
                .collect(Collectors.toList());
    }

    public boolean hasEntitlement(String featureCode) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return entitlementRepository.hasActiveEntitlement(tenantId, featureCode);
    }

    public boolean hasEntitlement(UUID tenantId, String featureCode) {
        return entitlementRepository.hasActiveEntitlement(tenantId, featureCode);
    }

    public EntitlementResponse getEntitlement(String featureCode) {
        UUID tenantId = TenantContext.getCurrentTenant();
        Entitlement entitlement = entitlementRepository.findByTenantIdAndFeatureCode(tenantId, featureCode)
                .orElseThrow(() -> new ResourceNotFoundException("Entitlement not found: " + featureCode));
        return EntitlementResponse.from(entitlement);
    }

    @Transactional
    public EntitlementResponse grantEntitlement(UUID productId, String featureCode, Integer limitValue, Instant expiresAt) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return grantEntitlement(tenantId, productId, featureCode, "manual", limitValue, expiresAt);
    }

    @Transactional
    public EntitlementResponse grantEntitlement(UUID tenantId, UUID productId, String featureCode, String source, Integer limitValue, Instant expiresAt) {
        Entitlement entitlement = Entitlement.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .productId(productId)
                .featureCode(featureCode)
                .source(source)
                .enabled(true)
                .limitValue(limitValue)
                .expiresAt(expiresAt)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        entitlement = entitlementRepository.save(entitlement);
        log.info("Granted {} entitlement {} to tenant {}", source, featureCode, tenantId);

        if (eventPublisher != null) {
            eventPublisher.publishEntitlementGranted(entitlement.getId(), tenantId, featureCode);
        }

        return EntitlementResponse.from(entitlement);
    }

    @Transactional
    public EntitlementResponse createEntitlement(CreateEntitlementRequest request) {
        UUID tenantId = request.getTenantId() != null ? request.getTenantId() : TenantContext.getCurrentTenant();
        return grantEntitlement(
                tenantId,
                request.getProductId(),
                request.getFeatureCode(),
                request.getSource(),
                request.getLimitValue(),
                request.getExpiresAt()
        );
    }

    @Transactional
    public void revokeEntitlement(UUID entitlementId) {
        UUID tenantId = TenantContext.getCurrentTenant();

        Entitlement entitlement = entitlementRepository.findById(entitlementId)
                .orElseThrow(() -> new ResourceNotFoundException("Entitlement not found: " + entitlementId));

        if (!entitlement.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Entitlement not found: " + entitlementId);
        }

        entitlementRepository.updateEnabled(entitlementId, false);
        log.info("Revoked entitlement {} for tenant {}", entitlementId, tenantId);

        if (eventPublisher != null) {
            eventPublisher.publishEntitlementRevoked(entitlementId, tenantId, entitlement.getFeatureCode());
        }
    }

    @Transactional
    public void revokeByTenantIdAndSource(UUID tenantId, String source) {
        entitlementRepository.deleteByTenantIdAndSource(tenantId, source);
        log.info("Revoked all {} entitlements for tenant {}", source, tenantId);

        if (eventPublisher != null) {
            eventPublisher.publishSubscriptionCancelled(tenantId);
        }
    }
}
