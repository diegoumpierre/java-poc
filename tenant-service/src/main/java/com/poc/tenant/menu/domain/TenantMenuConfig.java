package com.poc.tenant.menu.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("TNT_MENU_CONFIG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantMenuConfig implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MENU_ID")
    private String menuId;

    @Column("ENABLED")
    @Builder.Default
    private Boolean enabled = true;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Column("CREATED_BY")
    private UUID createdBy;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }

    public static TenantMenuConfig create(UUID tenantId, String menuId, Boolean enabled, UUID createdBy) {
        return TenantMenuConfig.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .menuId(menuId)
                .enabled(enabled)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy(createdBy)
                .build();
    }
}
