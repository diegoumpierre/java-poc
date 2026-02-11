package com.poc.kanban.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanAcceptanceCriteriaModel {

    private UUID id;

    @NotBlank(message = "Acceptance criteria text is required")
    @Size(min = 1, max = 500, message = "Acceptance criteria text must be between 1 and 500 characters")
    private String text;

    private Boolean completed;

    @Min(value = 0, message = "Position must be non-negative")
    private Integer position;
}
