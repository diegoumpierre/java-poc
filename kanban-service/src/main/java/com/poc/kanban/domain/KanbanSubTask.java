package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Table("KANB_SUBTASKS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanSubTask {

    @Id
    @Column("ID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("TEXT")
    private String text;

    @Column("COMPLETED")
    @Builder.Default
    private Boolean completed = false;

    @Column("POSITION")
    private Integer position;

    @Column("ASSIGNEE_USER_ID")
    private UUID assigneeUserId;

    @Column("DUE_DATE")
    private LocalDate dueDate;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
