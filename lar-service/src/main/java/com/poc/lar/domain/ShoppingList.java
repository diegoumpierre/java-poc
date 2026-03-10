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
@Table("LAR_SHOPPING_LISTS")
public class ShoppingList implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("NAME")
    private String name;

    @Column("STATUS")
    private String status;

    @Column("CREATED_BY")
    private UUID createdBy;

    @Column("ASSIGNED_TO")
    private UUID assignedTo;

    @Column("BUDGET_CENTS")
    private Integer budgetCents;

    @Column("ACTUAL_TOTAL_CENTS")
    private Integer actualTotalCents;

    @Column("COMPLETED_AT")
    private LocalDateTime completedAt;

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
