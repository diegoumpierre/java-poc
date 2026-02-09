package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("KANB_APPROVAL_RULES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRule implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TRANSITION_ID")
    private UUID transitionId;

    @Column("APPROVER_TYPE")
    private String approverType; // SPECIFIC_USER, BOARD_OWNER, CARD_ASSIGNEE, PERMISSION, ROLE

    @Column("APPROVER_VALUE")
    private String approverValue;

    @Column("FALLBACK_USER_ID")
    private UUID fallbackUserId;

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
