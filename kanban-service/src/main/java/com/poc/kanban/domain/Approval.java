package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("KANB_APPROVALS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Approval implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("CARD_ID")
    private UUID cardId;

    @Column("BOARD_ID")
    private UUID boardId;

    @Column("FROM_LIST_ID")
    private UUID fromListId;

    @Column("TO_LIST_ID")
    private UUID toListId;

    @Column("TRANSITION_ID")
    private UUID transitionId;

    @Column("APPROVAL_RULE_ID")
    private UUID approvalRuleId;

    @Column("REQUESTED_BY")
    private UUID requestedBy;

    @Column("REQUESTED_AT")
    private Instant requestedAt;

    @Column("RESOLVED_BY")
    private UUID resolvedBy;

    @Column("RESOLVED_AT")
    private Instant resolvedAt;

    @Column("STATUS")
    @Builder.Default
    private String status = "PENDING";

    @Column("COMMENT")
    private String comment;

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
