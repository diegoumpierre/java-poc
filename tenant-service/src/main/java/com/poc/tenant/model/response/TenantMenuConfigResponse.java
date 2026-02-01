package com.poc.tenant.model.response;

import com.poc.tenant.menu.domain.TenantMenuConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantMenuConfigResponse {
    private UUID id;
    private UUID tenantId;
    private String menuId;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    public static TenantMenuConfigResponse from(TenantMenuConfig config) {
        return TenantMenuConfigResponse.builder()
                .id(config.getId())
                .tenantId(config.getTenantId())
                .menuId(config.getMenuId())
                .enabled(config.getEnabled())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
