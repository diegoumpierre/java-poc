package com.poc.tenant.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("TNT_CORE_TENANTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant implements Persistable<UUID> {

    public static final UUID PLATFORM_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static final String TYPE_PLATFORM = "PLATFORM";
    public static final String TYPE_RESELLER = "RESELLER";
    public static final String TYPE_CLIENT = "CLIENT";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_SUSPENDED = "SUSPENDED";
    public static final String STATUS_BLOCKED = "BLOCKED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final String BILLING_DIRECT = "DIRECT";
    public static final String BILLING_WHOLESALE = "WHOLESALE";
    public static final String BILLING_COMMISSION = "COMMISSION";

    @Id
    @Column("ID")
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("NAME")
    private String name;

    @Column("SLUG")
    private String slug;

    @Column("PARENT_TENANT_ID")
    private UUID parentTenantId;

    @Column("TENANT_TYPE")
    @Builder.Default
    private String tenantType = TYPE_CLIENT;

    @Column("STATUS")
    @Builder.Default
    private String status = STATUS_ACTIVE;

    @Column("SUSPENDED_AT")
    private Instant suspendedAt;

    @Column("SUSPENSION_REASON")
    private String suspensionReason;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Column("TRIAL_ENDS_AT")
    private Instant trialEndsAt;

    @Column("SUBSCRIPTION_STATUS")
    @Builder.Default
    private String subscriptionStatus = "TRIAL";

    @Column("CREATED_BY")
    private UUID createdBy;

    @Column("DELETED_AT")
    private Instant deletedAt;

    @Column("DELETED_BY")
    private UUID deletedBy;

    @Column("BILLING_MODEL")
    @Builder.Default
    private String billingModel = "DIRECT";

    @Column("COMMISSION_RATE")
    private BigDecimal commissionRate;

    @Column("WHOLESALE_DISCOUNT")
    private BigDecimal wholesaleDiscount;

    @Column("FINANCE_WALLET_ID")
    private UUID financeWalletId;

    @Column("KANBAN_BOARD_ID")
    private UUID kanbanBoardId;

    @Column("PROVISIONING_STATUS")
    @Builder.Default
    private String provisioningStatus = "COMPLETE";

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete(UUID deletedByUserId) {
        this.deletedAt = Instant.now();
        this.deletedBy = deletedByUserId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }

    public boolean isPlatform() {
        return TYPE_PLATFORM.equals(tenantType);
    }

    public boolean isPartner() {
        return TYPE_RESELLER.equals(tenantType);
    }

    public boolean isClient() {
        return TYPE_CLIENT.equals(tenantType);
    }

    public boolean isActive() {
        return STATUS_ACTIVE.equals(status);
    }

    public boolean isSuspended() {
        return STATUS_SUSPENDED.equals(status) || STATUS_BLOCKED.equals(status);
    }

    public void suspend(String reason) {
        this.status = STATUS_SUSPENDED;
        this.suspendedAt = Instant.now();
        this.suspensionReason = reason;
    }

    public void activate() {
        this.status = STATUS_ACTIVE;
        this.suspendedAt = null;
        this.suspensionReason = null;
    }

    public boolean hasParent() {
        return parentTenantId != null;
    }
}
