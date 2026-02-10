package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table("KANB_CARDS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanCard {

    @Id
    @Column("ID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("TITLE")
    private String title;

    @Column("CARD_NUMBER")
    private Integer cardNumber;

    @Column("DESCRIPTION")
    private String description;

    @Column("START_DATE")
    private LocalDate startDate;

    @Column("DUE_DATE")
    private LocalDate dueDate;

    @Column("COMPLETED")
    @Builder.Default
    private Boolean completed = false;

    @Column("PROGRESS")
    @Builder.Default
    private Integer progress = 0;

    @Column("POSITION")
    private Integer position;

    @Column("PRIORITY_COLOR")
    private String priorityColor;

    @Column("PRIORITY_TITLE")
    private String priorityTitle;

    @Column("ATTACHMENTS")
    @Builder.Default
    private Integer attachments = 0;

    @Column("ASSIGNEE_USER_ID")
    private UUID assigneeUserId;

    @Column("SOURCE_SERVICE")
    private String sourceService;

    @Column("PENDING_APPROVAL")
    @Builder.Default
    private Boolean pendingApproval = false;

    @Column("PENDING_APPROVAL_ID")
    private UUID pendingApprovalId;

    @Column("PENDING_TARGET_LIST")
    private UUID pendingTargetList;

    @MappedCollection(idColumn = "CARD_ID", keyColumn = "POSITION")
    @Builder.Default
    private List<KanbanComment> comments = new ArrayList<>();

    @MappedCollection(idColumn = "CARD_ID", keyColumn = "POSITION")
    @Builder.Default
    private List<KanbanSubTask> subTasks = new ArrayList<>();

    @MappedCollection(idColumn = "CARD_ID", keyColumn = "POSITION")
    @Builder.Default
    private List<KanbanCardLabel> labels = new ArrayList<>();

    @MappedCollection(idColumn = "CARD_ID", keyColumn = "POSITION")
    @Builder.Default
    private List<KanbanAcceptanceCriteria> acceptanceCriteria = new ArrayList<>();

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
