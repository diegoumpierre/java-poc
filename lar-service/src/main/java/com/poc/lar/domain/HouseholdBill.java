package com.poc.lar.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LAR_HOUSEHOLD_BILLS")
public class HouseholdBill implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("NAME")
    private String name;

    @Column("CATEGORY")
    private String category;

    @Column("AMOUNT_CENTS")
    private Integer amountCents;

    @Column("DUE_DAY")
    private Integer dueDay;

    @Column("FREQUENCY")
    private String frequency;

    @Column("AUTO_PAY")
    private Boolean autoPay;

    @Column("RESPONSIBLE_MEMBER_ID")
    private UUID responsibleMemberId;

    @Column("NOTES")
    private String notes;

    @Column("ACTIVE")
    private Boolean active;

    @Column("CREATED_AT")
    private LocalDateTime createdAt;

    @Column("UPDATED_AT")
    private LocalDateTime updatedAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markAsExisting() {
        this.isNew = false;
    }
}
