package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("KANB_WORKFLOW_TRANSITIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTransition implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("BOARD_TYPE_CODE")
    private String boardTypeCode;

    @Column("FROM_STEP_ID")
    private UUID fromStepId;

    @Column("TO_STEP_ID")
    private UUID toStepId;

    @Column("REQUIRES_COMMENT")
    @Builder.Default
    private Boolean requiresComment = false;

    @Column("REQUIRES_APPROVAL")
    @Builder.Default
    private Boolean requiresApproval = false;

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
