package com.poc.tenant.tenant.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("TNT_ACC_ACCESS_REQUESTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessRequest implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("USER_ID")
    private UUID userId;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("MESSAGE")
    private String message;

    @Column("STATUS")
    @Builder.Default
    private String status = RequestStatus.PENDING.name();

    @Column("REVIEWED_BY")
    private UUID reviewedBy;

    @Column("REVIEWED_AT")
    private Instant reviewedAt;

    @Column("REJECTION_REASON")
    private String rejectionReason;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public boolean isPending() {
        return RequestStatus.PENDING.name().equals(status);
    }

    public void approve(UUID reviewerId) {
        this.status = RequestStatus.APPROVED.name();
        this.reviewedBy = reviewerId;
        this.reviewedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void reject(UUID reviewerId, String reason) {
        this.status = RequestStatus.REJECTED.name();
        this.reviewedBy = reviewerId;
        this.reviewedAt = Instant.now();
        this.rejectionReason = reason;
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
