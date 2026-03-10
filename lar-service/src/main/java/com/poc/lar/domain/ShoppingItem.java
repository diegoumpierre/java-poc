package com.poc.lar.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LAR_SHOPPING_ITEMS")
public class ShoppingItem implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("LIST_ID")
    private UUID listId;

    @Column("NAME")
    private String name;

    @Column("QUANTITY")
    private BigDecimal quantity;

    @Column("UNIT")
    private String unit;

    @Column("CATEGORY")
    private String category;

    @Column("ESTIMATED_PRICE_CENTS")
    private Integer estimatedPriceCents;

    @Column("ACTUAL_PRICE_CENTS")
    private Integer actualPriceCents;

    @Column("CHECKED")
    private Boolean checked;

    @Column("ADDED_BY")
    private UUID addedBy;

    @Column("CHECKED_BY")
    private UUID checkedBy;

    @Column("RECURRING")
    private Boolean recurring;

    @Column("NOTES")
    private String notes;

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
