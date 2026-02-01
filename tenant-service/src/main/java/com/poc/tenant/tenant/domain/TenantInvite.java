package com.poc.tenant.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("TNT_ACC_INVITES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantInvite implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("EMAIL")
    private String email;

    @Column("CODE")
    private String code;

    @Column("ROLE_IDS")
    private String roleIds;

    @Column("STATUS")
    @Builder.Default
    private String status = InviteStatus.PENDING.name();

    @Column("INVITED_BY")
    private UUID invitedBy;

    @Column("EXPIRES_AT")
    private Instant expiresAt;

    @Column("ACCEPTED_AT")
    private Instant acceptedAt;

    @Column("ACCEPTED_BY_USER_ID")
    private UUID acceptedByUserId;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    public enum InviteStatus {
        PENDING,
        ACCEPTED,
        EXPIRED,
        CANCELLED
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isPending() {
        return InviteStatus.PENDING.name().equals(status) && !isExpired();
    }

    public void accept(UUID userId) {
        this.status = InviteStatus.ACCEPTED.name();
        this.acceptedAt = Instant.now();
        this.acceptedByUserId = userId;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        this.status = InviteStatus.CANCELLED.name();
        this.updatedAt = Instant.now();
    }

    public void markExpired() {
        this.status = InviteStatus.EXPIRED.name();
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
