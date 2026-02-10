package com.poc.kanban.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanBoardModel {

    private UUID id;

    @NotBlank(message = "Board title is required")
    @Size(min = 1, max = 100, message = "Board title must be between 1 and 100 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,!?()]+$", message = "Board title contains invalid characters")
    private String title;

    @Size(max = 10, message = "Board code must be at most 10 characters")
    @Pattern(regexp = "^[A-Z0-9_-]*$", message = "Board code must contain only uppercase letters, numbers, underscores and hyphens")
    private String boardCode;

    private UUID userId;

    private UUID tenantId;

    @Valid
    private List<KanbanListModel> lists;

    @Valid
    private List<KanbanLabelModel> labels;

    private Instant createdAt;
    private Instant updatedAt;
}
