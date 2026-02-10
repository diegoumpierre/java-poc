package com.poc.kanban.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanAttachmentModel {

    private UUID id;
    private UUID cardId;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private Instant createdAt;
}
