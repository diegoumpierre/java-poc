package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("KANB_LABELS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanLabel {

    @Id
    @Column("ID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("NAME")
    private String name;

    @Column("COLOR")
    private String color;

    @Column("POSITION")
    private Integer position;

    @Column("CREATED_AT")
    private Instant createdAt;

    @Column("UPDATED_AT")
    private Instant updatedAt;
}
