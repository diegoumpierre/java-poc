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
@Table("LAR_CHECKLIST_RESPONSE_ITEMS")
public class ChecklistResponseItem implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("RESPONSE_ID")
    private UUID responseId;

    @Column("ITEM_ID")
    private UUID itemId;

    @Column("CHECKED")
    private Boolean checked;

    @Column("PHOTO_URL")
    private String photoUrl;

    @Column("NOTE")
    private String note;

    @Column("CREATED_AT")
    private LocalDateTime createdAt;

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
