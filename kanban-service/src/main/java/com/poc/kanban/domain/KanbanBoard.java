package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table("KANB_BOARDS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanBoard implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TITLE")
    private String title;

    @Column("BOARD_CODE")
    private String boardCode;

    @Column("USER_ID")
    private UUID userId;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("BOARD_TYPE_CODE")
    @Builder.Default
    private String boardTypeCode = "KANBAN";

    @Column("IS_ACTIVE")
    @Builder.Default
    private Boolean isActive = true;

    @MappedCollection(idColumn = "BOARD_ID", keyColumn = "POSITION")
    @Builder.Default
    private List<KanbanList> lists = new ArrayList<>();

    @MappedCollection(idColumn = "BOARD_ID", keyColumn = "POSITION")
    @Builder.Default
    private List<KanbanLabel> labels = new ArrayList<>();

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
