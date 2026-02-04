package com.poc.tenant.model.response;

import com.poc.tenant.menu.dto.MenuItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    private UUID id;
    private String menuKey;
    private String label;
    private String icon;
    private String to;  // routerLink for frontend
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
    private List<MenuItemResponse> items;
    private String createdAt;
    private String updatedAt;

    public static MenuItemResponse from(MenuItemDTO dto) {
        return MenuItemResponse.builder()
                .id(dto.getId())
                .menuKey(dto.getMenuKey())
                .label(dto.getLabel())
                .icon(dto.getIcon())
                .to(dto.getTo())
                .url(dto.getUrl())
                .target(dto.getTarget())
                .category(dto.getCategory())
                .featureCodes(dto.getFeatureCodes())
                .roles(dto.getRoles())
                .permissions(dto.getPermissions())
                .orderIndex(dto.getOrderIndex())
                .visible(dto.getVisible())
                .badge(dto.getBadge())
                .badgeClassName(dto.getBadgeClassName())
                .separator(dto.getSeparator())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
