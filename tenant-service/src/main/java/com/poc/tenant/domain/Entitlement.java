package com.poc.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("TNT_CORE_ENTITLEMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entitlement implements Persistable<UUID> {

    @Id
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("PRODUCT_ID")
    private UUID productId;

    @Column("FEATURE_CODE")
    private String featureCode;

    @Column("SOURCE")
    private String source; // subscription, manual, trial

    @Column("ENABLED")
    @Builder.Default
    private Boolean enabled = true;

    @Column("LIMIT_VALUE")
    private Integer limitValue;

    @Column("EXPIRES_AT")
    private Instant expiresAt;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
