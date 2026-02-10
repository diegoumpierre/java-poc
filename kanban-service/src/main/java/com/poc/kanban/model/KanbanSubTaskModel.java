package com.poc.kanban.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanSubTaskModel {

    private UUID id;

    @NotBlank(message = "Subtask text is required")
    @Size(min = 1, max = 500, message = "Subtask text must be between 1 and 500 characters")
    private String text;

    private Boolean completed;

    @Min(value = 0, message = "Position must be non-negative")
    private Integer position;

    @Valid
    private KanbanAssigneeModel assignee;

    private LocalDate dueDate;
}
