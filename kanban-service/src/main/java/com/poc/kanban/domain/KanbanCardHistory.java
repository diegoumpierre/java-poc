package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("KANB_CARD_HISTORY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanCardHistory implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("CARD_ID")
    private UUID cardId;

    @Column("BOARD_ID")
    private UUID boardId;

    @Column("CHANGED_BY")
    private UUID changedBy;

    @Column("CHANGED_BY_NAME")
    private String changedByName;

    @Column("CHANGE_TYPE")
    private String changeType;

    @Column("CHANGES_JSON")
    private String changesJson;

    @Column("COMMENT")
    private String comment;

    @Column("SNAPSHOT_JSON")
    private String snapshotJson;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
