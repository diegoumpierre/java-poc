package com.poc.lar.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("LAR_PANTRY_ITEMS")
public class PantryItem implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("NAME")
    private String name;

    @Column("QUANTITY")
    private BigDecimal quantity;

    @Column("UNIT")
    private String unit;

    @Column("CATEGORY")
    private String category;

    @Column("EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column("STATUS")
    private String status;

    @Column("AUTO_ADD_TO_LIST")
    private Boolean autoAddToList;

    @Column("PREFERRED_BRAND")
    private String preferredBrand;

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
