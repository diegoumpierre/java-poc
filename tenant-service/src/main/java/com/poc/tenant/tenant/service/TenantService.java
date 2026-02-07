package com.poc.tenant.tenant.service;

import com.poc.tenant.exception.ResourceNotFoundException;
import com.poc.tenant.model.request.CreateClientRequest;
import com.poc.tenant.model.request.CreatePartnerRequest;
import com.poc.tenant.model.request.SuspendTenantRequest;
import com.poc.tenant.model.request.TenantRequest;
import com.poc.tenant.model.response.TenantHierarchyStatsResponse;
import com.poc.tenant.model.response.TenantResponse;
import com.poc.tenant.model.response.TenantTreeNodeResponse;
import com.poc.tenant.tenant.domain.Tenant;
import com.poc.tenant.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;

    public List<TenantResponse> findAll() {
        return tenantRepository.findAllActive().stream()
                .map(TenantResponse::from)
                .collect(Collectors.toList());
    }

    public TenantResponse findById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
        return TenantResponse.from(tenant);
    }

    public TenantResponse findBySlug(String slug) {
        Tenant tenant = tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + slug));
        return TenantResponse.from(tenant);
    }

    @Transactional
    public TenantResponse create(TenantRequest request) {
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Tenant with slug already exists: " + request.getSlug());
        }

        String tenantType = request.getTenantType() != null ?
                request.getTenantType() : Tenant.TYPE_CLIENT;

        UUID parentTenantId = request.getParentTenantId() != null ?
                request.getParentTenantId() : Tenant.PLATFORM_TENANT_ID;

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .slug(request.getSlug())
                .parentTenantId(parentTenantId)
                .tenantType(tenantType)
                .status(Tenant.STATUS_ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        tenant = tenantRepository.save(tenant);
        log.info("Created tenant: {} (type={}, parent={})", tenant.getSlug(), tenantType, parentTenantId);
        return TenantResponse.from(tenant);
    }

    @Transactional
    public TenantResponse update(UUID id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));

        if (!tenant.getSlug().equals(request.getSlug()) && tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Tenant with slug already exists: " + request.getSlug());
        }

        tenant.setName(request.getName());
        tenant.setSlug(request.getSlug());
        tenant.setUpdatedAt(Instant.now());
        tenant.markNotNew();

        tenant = tenantRepository.save(tenant);
        log.info("Updated tenant: {}", tenant.getSlug());
        return TenantResponse.from(tenant);
    }

    @Transactional
    public void delete(UUID id, UUID deletedBy) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));

        if (tenant.isDeleted()) {
            throw new IllegalStateException("Tenant already deleted");
        }

        tenant.softDelete(deletedBy);
        tenant.setUpdatedAt(Instant.now());
        tenant.markNotNew();
        tenantRepository.save(tenant);

        log.info("Tenant soft deleted: {} by {}", tenant.getSlug(), deletedBy);
    }

    public List<TenantResponse> search(String query) {
        return tenantRepository.searchByName(query).stream()
                .map(TenantResponse::from)
                .collect(Collectors.toList());
    }

    public List<TenantResponse> findByParentId(UUID parentId) {
        return tenantRepository.findByParentTenantId(parentId).stream()
                .map(TenantResponse::from)
                .collect(Collectors.toList());
    }

    public Tenant getTenantEntity(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));
    }

    // ==================== HIERARCHY OPERATIONS ====================

    public TenantTreeNodeResponse getHierarchyTree() {
        Tenant platform = tenantRepository.findPlatformTenant()
                .orElseThrow(() -> new ResourceNotFoundException("Platform tenant not found"));

        return buildTreeNode(platform);
    }

    private TenantTreeNodeResponse buildTreeNode(Tenant tenant) {
        List<Tenant> children = tenantRepository.findByParentTenantId(tenant.getId());
        int childCount = children.size();

        TenantTreeNodeResponse node = TenantTreeNodeResponse.from(tenant, childCount);

        for (Tenant child : children) {
            node.getChildren().add(buildTreeNode(child));
        }

        return node;
    }

    public TenantHierarchyStatsResponse getHierarchyStats() {
        return TenantHierarchyStatsResponse.builder()
                .totalTenants(tenantRepository.countTotal())
                .totalPartners(tenantRepository.countPartners())
                .totalClients(tenantRepository.countClients())
                .activePartners(tenantRepository.countActivePartners())
                .activeClients(tenantRepository.countActiveClients())
                .suspendedTenants(tenantRepository.countSuspended())
                .tenantsOnTrial(tenantRepository.countOnTrial())
                .build();
    }

    public List<TenantResponse> getHierarchyChildren(UUID parentId, String type) {
        List<Tenant> children;
        if (parentId != null) {
            children = tenantRepository.findByParentTenantId(parentId);
        } else if (type != null) {
            children = tenantRepository.findByTenantType(type);
        } else {
            children = tenantRepository.findAllActive();
        }

        if (type != null && parentId != null) {
            children = children.stream()
                    .filter(t -> type.equals(t.getTenantType()))
                    .collect(Collectors.toList());
        }

        return children.stream()
                .map(TenantResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TenantResponse createPartner(CreatePartnerRequest request) {
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Tenant with slug already exists: " + request.getSlug());
        }

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .slug(request.getSlug())
                .parentTenantId(Tenant.PLATFORM_TENANT_ID)
                .tenantType(Tenant.TYPE_RESELLER)
                .status(Tenant.STATUS_ACTIVE)
                .subscriptionStatus("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        tenant = tenantRepository.save(tenant);
        log.info("Created partner tenant: {} with admin: {}", tenant.getSlug(), request.getAdminEmail());

        // Note: Admin user creation should be handled by auth-service via event or API call
        // For now, we just create the tenant

        return TenantResponse.from(tenant);
    }

    @Transactional
    public TenantResponse createClient(CreateClientRequest request, UUID parentId) {
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Tenant with slug already exists: " + request.getSlug());
        }

        UUID effectiveParentId = parentId != null ? parentId : Tenant.PLATFORM_TENANT_ID;

        // Validate parent exists and is not a CLIENT
        Tenant parent = tenantRepository.findById(effectiveParentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent tenant not found: " + effectiveParentId));

        if (Tenant.TYPE_CLIENT.equals(parent.getTenantType())) {
            throw new IllegalArgumentException("Cannot create client under another client");
        }

        Instant trialEndsAt = null;
        String subscriptionStatus = "ACTIVE";
        if (request.getTrialDays() != null && request.getTrialDays() > 0) {
            trialEndsAt = Instant.now().plus(request.getTrialDays(), ChronoUnit.DAYS);
            subscriptionStatus = "TRIAL";
        }

        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .slug(request.getSlug())
                .parentTenantId(effectiveParentId)
                .tenantType(Tenant.TYPE_CLIENT)
                .status(Tenant.STATUS_ACTIVE)
                .subscriptionStatus(subscriptionStatus)
                .trialEndsAt(trialEndsAt)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        tenant = tenantRepository.save(tenant);
        log.info("Created client tenant: {} under parent {} with admin: {}",
                tenant.getSlug(), effectiveParentId, request.getAdminEmail());

        // Note: Admin user creation should be handled by auth-service via event or API call

        return TenantResponse.from(tenant);
    }

    @Transactional
    public TenantResponse suspendTenant(UUID id, SuspendTenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));

        if (tenant.isPlatform()) {
            throw new IllegalArgumentException("Cannot suspend the platform tenant");
        }

        tenant.suspend(request.getReason());
        tenant.setUpdatedAt(Instant.now());
        tenant.markNotNew();
        tenantRepository.save(tenant);

        log.info("Suspended tenant: {} - reason: {}", tenant.getSlug(), request.getReason());

        // Optionally suspend children
        if (Boolean.TRUE.equals(request.getSuspendChildren())) {
            List<Tenant> children = tenantRepository.findByParentTenantId(id);
            for (Tenant child : children) {
                child.suspend("Parent suspended: " + request.getReason());
                child.setUpdatedAt(Instant.now());
                child.markNotNew();
                tenantRepository.save(child);
                log.info("Suspended child tenant: {}", child.getSlug());
            }
        }

        return TenantResponse.from(tenant);
    }

    @Transactional
    public TenantResponse activateTenant(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found: " + id));

        if (tenant.isPlatform()) {
            throw new IllegalArgumentException("Cannot activate the platform tenant (already active)");
        }

        tenant.activate();
        tenant.setUpdatedAt(Instant.now());
        tenant.markNotNew();
        tenantRepository.save(tenant);

        log.info("Activated tenant: {}", tenant.getSlug());

        return TenantResponse.from(tenant);
    }
}
