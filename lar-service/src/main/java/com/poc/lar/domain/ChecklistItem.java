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
@Table("LAR_CHECKLIST_ITEMS")
public class ChecklistItem implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TEMPLATE_ID")
    private UUID templateId;

    @Column("DESCRIPTION")
    private String description;

    @Column("ORDER_INDEX")
    private Integer orderIndex;

    @Column("REQUIRED")
    private Boolean required;

    @Column("REQUIRES_PHOTO")
    private Boolean requiresPhoto;

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
