package com.poc.tenant.menu.service;

import com.poc.tenant.menu.controller.TenantMenuConfigController.TenantMenuConfigRequest;
import com.poc.tenant.menu.domain.TenantMenuConfig;
import com.poc.tenant.menu.repository.TenantMenuConfigRepository;
import com.poc.tenant.model.response.TenantMenuConfigResponse;
import com.poc.tenant.tenant.domain.Tenant;
import com.poc.tenant.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantMenuConfigService {

    private final TenantMenuConfigRepository tenantMenuConfigRepository;
    private final TenantRepository tenantRepository;

    /**
     * Get effective disabled menu IDs for a tenant, including restrictions from parent tenants.
     * A menu is disabled if it's disabled in any tenant in the hierarchy chain.
     */
    public List<String> getEffectiveDisabledMenuIds(UUID tenantId) {
        Set<String> disabledMenuIds = new HashSet<>();

        // Get disabled menus from current tenant and all ancestors
        List<UUID> tenantHierarchy = getTenantHierarchy(tenantId);

        for (UUID tId : tenantHierarchy) {
            List<TenantMenuConfig> configs = tenantMenuConfigRepository.findDisabledByTenantId(tId);
            for (TenantMenuConfig config : configs) {
                disabledMenuIds.add(config.getMenuId());
            }
        }

        return new ArrayList<>(disabledMenuIds);
    }

    /**
     * Get tenant hierarchy from current tenant up to platform (inclusive).
     * Returns list ordered from current tenant to platform.
     */
    private List<UUID> getTenantHierarchy(UUID tenantId) {
        List<UUID> hierarchy = new ArrayList<>();

        if (tenantId == null) {
            return hierarchy;
        }

        UUID currentId = tenantId;
        int maxDepth = 10; // Prevent infinite loops
        int depth = 0;

        while (currentId != null && depth < maxDepth) {
            hierarchy.add(currentId);

            Optional<Tenant> tenant = tenantRepository.findById(currentId);
            if (tenant.isEmpty()) {
                break;
            }

            currentId = tenant.get().getParentTenantId();
            depth++;
        }

        return hierarchy;
    }

    /**
     * Get menu configurations for a specific tenant (not including parent configs).
     */
    public List<TenantMenuConfigResponse> getMenuConfigs(UUID tenantId) {
        if (tenantId == null) {
            return Collections.emptyList();
        }

        return tenantMenuConfigRepository.findByTenantId(tenantId).stream()
                .map(TenantMenuConfigResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Save menu configurations for a tenant.
     */
    @Transactional
    public List<TenantMenuConfigResponse> saveMenuConfigs(UUID tenantId, List<TenantMenuConfigRequest> requests, UUID userId) {
        if (tenantId == null || requests == null) {
            return Collections.emptyList();
        }

        List<TenantMenuConfigResponse> responses = new ArrayList<>();

        for (TenantMenuConfigRequest request : requests) {
            TenantMenuConfig config = saveOrUpdateConfig(tenantId, request.menuId(), request.enabled(), userId);
            responses.add(TenantMenuConfigResponse.from(config));
        }

        return responses;
    }

    /**
     * Toggle a specific menu item for a tenant.
     */
    @Transactional
    public TenantMenuConfigResponse toggleMenu(UUID tenantId, String menuId, Boolean enabled, UUID userId) {
        TenantMenuConfig config = saveOrUpdateConfig(tenantId, menuId, enabled, userId);
        return TenantMenuConfigResponse.from(config);
    }

    /**
     * Copy menu configuration from parent tenant.
     */
    @Transactional
    public void copyFromParent(UUID tenantId, UUID userId) {
        if (tenantId == null) {
            return;
        }

        // Get parent tenant
        Optional<Tenant> tenant = tenantRepository.findById(tenantId);
        if (tenant.isEmpty() || tenant.get().getParentTenantId() == null) {
            log.warn("Cannot copy from parent: tenant {} has no parent", tenantId);
            return;
        }

        UUID parentTenantId = tenant.get().getParentTenantId();

        // Delete existing configs for this tenant
        List<TenantMenuConfig> existingConfigs = tenantMenuConfigRepository.findByTenantId(tenantId);
        for (TenantMenuConfig config : existingConfigs) {
            tenantMenuConfigRepository.delete(config);
        }

        // Copy configs from parent
        List<TenantMenuConfig> parentConfigs = tenantMenuConfigRepository.findByTenantId(parentTenantId);
        for (TenantMenuConfig parentConfig : parentConfigs) {
            TenantMenuConfig newConfig = TenantMenuConfig.create(
                    tenantId,
                    parentConfig.getMenuId(),
                    parentConfig.getEnabled(),
                    userId
            );
            tenantMenuConfigRepository.save(newConfig);
        }

        log.info("Copied {} menu configs from parent {} to tenant {}", parentConfigs.size(), parentTenantId, tenantId);
    }

    private TenantMenuConfig saveOrUpdateConfig(UUID tenantId, String menuId, Boolean enabled, UUID userId) {
        Optional<TenantMenuConfig> existing = tenantMenuConfigRepository.findByTenantIdAndMenuId(tenantId, menuId);

        TenantMenuConfig config;
        if (existing.isPresent()) {
            config = existing.get();
            config.setEnabled(enabled);
            config.setUpdatedAt(Instant.now());
            config.markNotNew();
        } else {
            config = TenantMenuConfig.create(tenantId, menuId, enabled, userId);
        }

        return tenantMenuConfigRepository.save(config);
    }
}
