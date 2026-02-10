package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("KANB_WORKFLOW_STEPS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStep implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("BOARD_TYPE_CODE")
    private String boardTypeCode;

    @Column("STEP_CODE")
    private String stepCode;

    @Column("STEP_NAME")
    private String stepName;

    @Column("POSITION")
    @Builder.Default
    private Integer position = 0;

    @Column("COLOR")
    private String color;

    @Column("IS_INITIAL")
    @Builder.Default
    private Boolean isInitial = false;

    @Column("IS_FINAL")
    @Builder.Default
    private Boolean isFinal = false;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;

    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}
