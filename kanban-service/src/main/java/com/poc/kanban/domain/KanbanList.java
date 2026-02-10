package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table("KANB_LISTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanList {

    @Id
    @Column("ID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("TITLE")
    private String title;

    @Column("POSITION")
    private Integer position;

    @Column("WORKFLOW_STEP_ID")
    private UUID workflowStepId;

    @Column("WIP_LIMIT")
    private Integer wipLimit;

    @Column("COLOR")
    private String color;

    @MappedCollection(idColumn = "LIST_ID", keyColumn = "POSITION")
    @Builder.Default
    private List<KanbanCard> cards = new ArrayList<>();

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
