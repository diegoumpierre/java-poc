package com.poc.tenant.menu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.tenant.menu.domain.MenuItem;
import com.poc.tenant.menu.dto.MenuItemDTO;
import com.poc.tenant.menu.repository.MenuItemRepository;
import com.poc.tenant.model.response.MenuItemResponse;
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
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CachedMenuService cachedMenuService;
    private final TenantMenuConfigService tenantMenuConfigService;
    private final MenuFilterService menuFilterService;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Returns menus for a tenant WITHOUT access filtering (for admin endpoints).
     * DB handles: VISIBLE + disabled menus filtering.
     */
    public List<MenuItemResponse> getUnfilteredMenusForTenant(UUID tenantId) {
        // Step 1: Load filter criteria FIRST
        Set<String> disabledMenus = new HashSet<>(tenantMenuConfigService.getEffectiveDisabledMenuIds(tenantId));

        // Step 2: Query DB already filtered (visible + not-disabled)
        List<MenuItemDTO> menus = queryVisibleMenus(disabledMenus);

        return buildMenuTree(menus);
    }

    /**
     * Returns menus for a tenant WITH access filtering by entitlements, roles, and permissions.
     * DB handles: VISIBLE + disabled menus filtering.
     * Memory handles: entitlements/roles/permissions (logic too complex for SQL).
     */
    public List<MenuItemResponse> getMenusForTenant(UUID tenantId, Set<String> userRoles, Set<String> userPermissions) {
        // Step 1: Load ALL filter criteria FIRST
        Set<String> disabledMenus = new HashSet<>(tenantMenuConfigService.getEffectiveDisabledMenuIds(tenantId));
        Set<String> entitlements = menuFilterService.loadEntitlements(tenantId);

        // Step 2: Query DB already filtered (visible + not-disabled)
        List<MenuItemDTO> menus = queryVisibleMenus(disabledMenus);

        // Step 3: Apply access control in memory (category-based logic can't be in SQL)
        List<MenuItemDTO> filtered = menus.stream()
                .filter(m -> menuFilterService.isAccessible(m, entitlements, userRoles, userPermissions))
                .collect(Collectors.toList());

        List<MenuItemDTO> cleaned = menuFilterService.removeEmptyParents(filtered);

        return buildMenuTree(cleaned);
    }

    /**
     * Returns all visible platform menus (uses Redis cache, no tenant filtering).
     */
    public List<MenuItemResponse> getPlatformMenus() {
        List<MenuItemDTO> allMenus = cachedMenuService.getAllMenus();

        List<MenuItemDTO> visibleMenus = allMenus.stream()
                .filter(m -> Boolean.TRUE.equals(m.getVisible()))
                .collect(Collectors.toList());

        return buildMenuTree(visibleMenus);
    }

    /**
     * Returns ALL platform menus (including non-visible) for admin management.
     */
    public List<MenuItemResponse> getAllMenusForAdmin() {
        List<MenuItem> entities = menuItemRepository.findAllOrderByOrderIndex();
        List<MenuItemDTO> dtos = entities.stream()
                .map(MenuItemDTO::from)
                .collect(Collectors.toList());
        return buildMenuTree(dtos);
    }

    @Transactional
    public MenuItemResponse createMenuItem(UUID parentId, String menuKey, String label, String icon,
                                           String route, String url, String target, String category,
                                           List<String> featureCodes, List<String> roles, List<String> permissions,
                                           Integer orderIndex, Boolean visible, String badge, String badgeClass,
                                           Boolean separator) {
        MenuItem item = MenuItem.builder()
                .id(UUID.randomUUID())
                .parentId(parentId)
                .menuKey(menuKey)
                .label(label)
                .icon(icon)
                .route(route)
                .url(url)
                .target(target)
                .category(category != null ? category : "AUTHENTICATED")
                .featureCodes(toJsonArray(featureCodes))
                .roles(toJsonArray(roles))
                .permissions(toJsonArray(permissions))
                .orderIndex(orderIndex != null ? orderIndex : 0)
                .visible(visible != null ? visible : true)
                .badge(badge)
                .badgeClass(badgeClass)
                .separator(separator != null ? separator : false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .isNew(true)
                .build();

        MenuItem saved = menuItemRepository.save(item);
        cachedMenuService.evictCache();
        return MenuItemResponse.from(MenuItemDTO.from(saved));
    }

    @Transactional
    public MenuItemResponse updateMenuItem(UUID id, UUID parentId, String menuKey, String label, String icon,
                                           String route, String url, String target, String category,
                                           List<String> featureCodes, List<String> roles, List<String> permissions,
                                           Integer orderIndex, Boolean visible, String badge, String badgeClass,
                                           Boolean separator) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));

        item.markNotNew();
        if (parentId != null) item.setParentId(parentId);
        if (menuKey != null) item.setMenuKey(menuKey);
        if (label != null) item.setLabel(label);
        item.setIcon(icon);
        item.setRoute(route);
        item.setUrl(url);
        item.setTarget(target);
        if (category != null) item.setCategory(category);
        item.setFeatureCodes(toJsonArray(featureCodes));
        item.setRoles(toJsonArray(roles));
        item.setPermissions(toJsonArray(permissions));
        if (orderIndex != null) item.setOrderIndex(orderIndex);
        if (visible != null) item.setVisible(visible);
        item.setBadge(badge);
        item.setBadgeClass(badgeClass);
        if (separator != null) item.setSeparator(separator);
        item.setUpdatedAt(Instant.now());

        MenuItem saved = menuItemRepository.save(item);
        cachedMenuService.evictCache();
        return MenuItemResponse.from(MenuItemDTO.from(saved));
    }

    @Transactional
    public void deleteMenuItem(UUID id) {
        // Delete children first
        List<MenuItem> all = menuItemRepository.findAllOrderByOrderIndex();
        Set<UUID> toDelete = new LinkedHashSet<>();
        collectDescendants(id, all, toDelete);
        toDelete.add(id);

        menuItemRepository.deleteAllById(toDelete);
        cachedMenuService.evictCache();
    }

    @Transactional
    public MenuItemResponse moveMenuItem(UUID id, UUID newParentId, Integer newOrderIndex) {
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + id));

        item.markNotNew();
        item.setParentId(newParentId);
        if (newOrderIndex != null) item.setOrderIndex(newOrderIndex);
        item.setUpdatedAt(Instant.now());

        // Reorder siblings at destination
        List<MenuItem> all = menuItemRepository.findAllOrderByOrderIndex();
        List<MenuItem> siblings = all.stream()
                .filter(m -> Objects.equals(m.getParentId(), newParentId) && !m.getId().equals(id))
                .sorted(Comparator.comparingInt(m -> m.getOrderIndex() != null ? m.getOrderIndex() : 0))
                .collect(Collectors.toList());

        int targetIndex = newOrderIndex != null ? newOrderIndex : siblings.size();

        int idx = 0;
        for (MenuItem sibling : siblings) {
            if (idx == targetIndex) idx++; // skip the slot for the moved item
            sibling.markNotNew();
            sibling.setOrderIndex(idx);
            sibling.setUpdatedAt(Instant.now());
            menuItemRepository.save(sibling);
            idx++;
        }
        item.setOrderIndex(targetIndex);

        MenuItem saved = menuItemRepository.save(item);
        cachedMenuService.evictCache();
        return MenuItemResponse.from(MenuItemDTO.from(saved));
    }

    private void collectDescendants(UUID parentId, List<MenuItem> all, Set<UUID> result) {
        for (MenuItem item : all) {
            if (parentId.equals(item.getParentId())) {
                result.add(item.getId());
                collectDescendants(item.getId(), all, result);
            }
        }
    }

    private String toJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return MAPPER.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Query visible menus from DB, excluding disabled menu keys.
     */
    private List<MenuItemDTO> queryVisibleMenus(Set<String> disabledMenuKeys) {
        List<com.poc.tenant.menu.domain.MenuItem> entities;
        if (disabledMenuKeys.isEmpty()) {
            entities = menuItemRepository.findVisibleOrderByOrderIndex();
        } else {
            entities = menuItemRepository.findVisibleExcludingKeys(disabledMenuKeys);
        }
        return entities.stream()
                .map(MenuItemDTO::from)
                .collect(Collectors.toList());
    }

    private List<MenuItemResponse> buildMenuTree(List<MenuItemDTO> allMenus) {
        Map<UUID, MenuItemDTO> menuMap = new HashMap<>();
        Map<UUID, List<MenuItemDTO>> childrenMap = new HashMap<>();
        List<MenuItemDTO> rootMenus = new ArrayList<>();

        for (MenuItemDTO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
            childrenMap.put(menu.getId(), new ArrayList<>());
        }

        for (MenuItemDTO menu : allMenus) {
            if (menu.getParentId() == null) {
                rootMenus.add(menu);
            } else {
                List<MenuItemDTO> children = childrenMap.get(menu.getParentId());
                if (children != null) {
                    children.add(menu);
                }
            }
        }

        rootMenus.sort(Comparator.comparingInt(m -> m.getOrderIndex() != null ? m.getOrderIndex() : 0));

        return rootMenus.stream()
                .map(m -> toResponse(m, childrenMap))
                .collect(Collectors.toList());
    }

    private MenuItemResponse toResponse(MenuItemDTO menu, Map<UUID, List<MenuItemDTO>> childrenMap) {
        MenuItemResponse response = MenuItemResponse.from(menu);

        List<MenuItemDTO> children = childrenMap.get(menu.getId());
        if (children != null && !children.isEmpty()) {
            children.sort(Comparator.comparingInt(m -> m.getOrderIndex() != null ? m.getOrderIndex() : 0));
            response.setItems(children.stream()
                    .map(c -> toResponse(c, childrenMap))
                    .collect(Collectors.toList()));
        }

        return response;
    }
}
