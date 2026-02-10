package com.poc.kanban.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("KANB_CARD_LABELS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KanbanCardLabel {

    @Id
    @Column("ID")
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column("LABEL_ID")
    private UUID labelId;

    @Column("POSITION")
    private Integer position;
}
