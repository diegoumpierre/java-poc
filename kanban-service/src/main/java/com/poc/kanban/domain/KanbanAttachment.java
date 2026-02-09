package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("KANB_ATTACHMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanAttachment implements Persistable<UUID> {

    @Id
    @Column("ID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Column("CARD_ID")
    private UUID cardId;

    @Column("FILE_NAME")
    private String fileName;

    @Column("FILE_PATH")
    private String filePath;

    @Column("FILE_SIZE")
    private Long fileSize;

    @Column("CONTENT_TYPE")
    private String contentType;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
