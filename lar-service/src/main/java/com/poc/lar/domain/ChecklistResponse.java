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
@Table("LAR_CHECKLIST_RESPONSES")
public class ChecklistResponse implements Persistable<UUID> {

    @Id
    @Column("ID")
    private UUID id;

    @Column("TENANT_ID")
    private UUID tenantId;

    @Column("TEMPLATE_ID")
    private UUID templateId;

    @Column("OUTING_ID")
    private UUID outingId;

    @Column("MEMBER_ID")
    private UUID memberId;

    @Column("ALL_PASSED")
    private Boolean allPassed;

    @Column("COMPLETED_AT")
    private LocalDateTime completedAt;

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
