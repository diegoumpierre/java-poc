package com.poc.tenant.menu.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.tenant.menu.domain.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDTO {
    private UUID id;
    private UUID parentId;
    private String menuKey;
    private String label;
    private String icon;
    private String to;
    private String url;
    private String target;
    private String category;
    private List<String> featureCodes;
    private List<String> roles;
    private List<String> permissions;
    private Integer orderIndex;
    private Boolean visible;
    private String badge;
    private String badgeClassName;
    private Boolean separator;
    private String createdAt;
    private String updatedAt;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static MenuItemDTO from(MenuItem entity) {
        return MenuItemDTO.builder()
                .id(entity.getId())
                .parentId(entity.getParentId())
                .menuKey(entity.getMenuKey())
                .label(entity.getLabel())
                .icon(entity.getIcon())
                .to(entity.getRoute())
                .url(entity.getUrl())
                .target(entity.getTarget())
                .category(entity.getCategory())
                .featureCodes(parseJsonArray(entity.getFeatureCodes()))
                .roles(parseJsonArray(entity.getRoles()))
                .permissions(parseJsonArray(entity.getPermissions()))
                .orderIndex(entity.getOrderIndex())
                .visible(entity.getVisible())
                .badge(entity.getBadge())
                .badgeClassName(entity.getBadgeClass())
                .separator(entity.getSeparator())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }

    private static List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
