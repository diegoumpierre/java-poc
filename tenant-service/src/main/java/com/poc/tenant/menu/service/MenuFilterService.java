package com.poc.tenant.menu.service;

import com.poc.tenant.menu.dto.MenuItemDTO;
import com.poc.tenant.domain.Entitlement;
import com.poc.tenant.repository.EntitlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuFilterService {

    private final EntitlementRepository entitlementRepository;

    /**
     * Filters menus based on tenant entitlements, user roles, and user permissions.
     * Loads entitlements from DB internally.
     */
    public List<MenuItemDTO> filterMenus(
            List<MenuItemDTO> menus,
            UUID tenantId,
            Set<String> userRoles,
            Set<String> userPermissions) {

        Set<String> entitlements = loadEntitlements(tenantId);
        return filterMenus(menus, entitlements, userRoles, userPermissions);
    }

    /**
     * Filters menus using pre-loaded entitlements (avoids extra DB query).
     * Use this when entitlements are already available from a prior load.
     */
    public List<MenuItemDTO> filterMenus(
            List<MenuItemDTO> menus,
            Set<String> entitlements,
            Set<String> userRoles,
            Set<String> userPermissions) {

        // Phase 1: filter individual menus by accessibility
        List<MenuItemDTO> accessible = menus.stream()
                .filter(menu -> isAccessible(menu, entitlements, userRoles, userPermissions))
                .collect(Collectors.toList());

        // Phase 2: remove empty parent groups (to == null and no surviving children)
        return removeEmptyParents(accessible);
    }

    /**
     * Load active entitlement feature codes for a tenant.
     */
    public Set<String> loadEntitlements(UUID tenantId) {
        if (tenantId == null) {
            return Collections.emptySet();
        }
        return entitlementRepository.findActiveByTenantId(tenantId).stream()
                .map(Entitlement::getFeatureCode)
                .collect(Collectors.toSet());
    }

    /**
     * Check if a menu item is accessible given entitlements, roles, and permissions.
     */
    public boolean isAccessible(
            MenuItemDTO menu,
            Set<String> entitlements,
            Set<String> userRoles,
            Set<String> userPermissions) {

        String category = menu.getCategory();
        if (category == null || category.isBlank()) {
            return true; // no category = always visible
        }

        switch (category.toUpperCase()) {
            case "AUTHENTICATED":
                return true;

            case "ENTITLEMENT":
            case "FEATURE_GATED":
                return hasAnyFeatureCode(menu, entitlements);

            case "ADMIN":
                return hasAnyRole(menu, userRoles)
                        || (!userPermissions.isEmpty() && hasAnyRoleInferredFromPermissions(menu, userPermissions));

            case "SUPER_ADMIN":
                return hasAnyPermission(menu, userPermissions);

            case "PLATFORM_ONLY":
                return hasAnyFeatureCode(menu, entitlements)
                        || userPermissions.contains("PLATFORM_ADMIN");

            case "DEMO":
                return false;

            case "CUSTOMER":
                return userPermissions.contains("PORTAL_ACCESS");

            default:
                log.warn("Unknown menu category '{}' for menu '{}'", category, menu.getMenuKey());
                return false;
        }
    }

    private boolean hasAnyFeatureCode(MenuItemDTO menu, Set<String> entitlements) {
        List<String> featureCodes = menu.getFeatureCodes();
        if (featureCodes == null || featureCodes.isEmpty()) {
            return false;
        }
        return featureCodes.stream().anyMatch(entitlements::contains);
    }

    private boolean hasAnyRole(MenuItemDTO menu, Set<String> userRoles) {
        List<String> menuRoles = menu.getRoles();
        if (menuRoles == null || menuRoles.isEmpty()) {
            return false;
        }
        // Case-insensitive: seed data has lowercase "admin", header has uppercase "ADMIN"
        return menuRoles.stream()
                .map(String::toUpperCase)
                .anyMatch(userRoles::contains);
    }

    /**
     * When X-User-Roles header is not available (gateway only sends X-User-Permissions),
     * infer role access from permissions. Users with any permission are at least 'manager'.
     * Users with PLATFORM_ADMIN are 'admin'.
     */
    private boolean hasAnyRoleInferredFromPermissions(MenuItemDTO menu, Set<String> userPermissions) {
        List<String> menuRoles = menu.getRoles();
        if (menuRoles == null || menuRoles.isEmpty()) {
            return false;
        }
        for (String role : menuRoles) {
            String r = role.toUpperCase();
            if ("ADMIN".equals(r) && userPermissions.contains("PLATFORM_ADMIN")) {
                return true;
            }
            if ("MANAGER".equals(r) && !userPermissions.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyPermission(MenuItemDTO menu, Set<String> userPermissions) {
        List<String> menuPermissions = menu.getPermissions();
        if (menuPermissions == null || menuPermissions.isEmpty()) {
            return false;
        }
        return menuPermissions.stream().anyMatch(userPermissions::contains);
    }

    /**
     * Remove parent group menus that have no surviving children.
     * A parent group is identified by: to == null (no route, just a container).
     */
    public List<MenuItemDTO> removeEmptyParents(List<MenuItemDTO> menus) {
        // Build set of all surviving menu IDs
        Set<UUID> survivingIds = menus.stream()
                .map(MenuItemDTO::getId)
                .collect(Collectors.toSet());

        // Build map: parentId -> count of surviving children
        Map<UUID, Long> childrenCount = menus.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(MenuItemDTO::getParentId, Collectors.counting()));

        // Remove parents that are containers (to == null) with no surviving children
        return menus.stream()
                .filter(menu -> {
                    if (menu.getTo() != null && !menu.getTo().isBlank()) {
                        return true; // has a route, keep it
                    }
                    // Container: keep only if it has surviving children
                    return childrenCount.getOrDefault(menu.getId(), 0L) > 0;
                })
                .collect(Collectors.toList());
    }
}
