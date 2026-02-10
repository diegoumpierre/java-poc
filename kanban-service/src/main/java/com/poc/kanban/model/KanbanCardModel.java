package com.poc.kanban.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanCardModel {

    private UUID id;

    @Size(max = 50, message = "Card ID must be at most 50 characters")
    private String cardId;

    @Size(max = 20, message = "Card code must be at most 20 characters")
    private String cardCode;

    @NotBlank(message = "Card title is required")
    @Size(min = 1, max = 255, message = "Card title must be between 1 and 255 characters")
    private String title;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    private LocalDate startDate;
    private LocalDate dueDate;
    private Boolean completed;

    @Min(value = 0, message = "Progress must be between 0 and 100")
    @Max(value = 100, message = "Progress must be between 0 and 100")
    private Integer progress;

    @Min(value = 0, message = "Position must be non-negative")
    private Integer position;

    @Valid
    private KanbanPriorityModel priority;

    @Min(value = 0, message = "Attachments count must be non-negative")
    private Integer attachments;

    @Valid
    private KanbanAssigneeModel assignee;

    @Valid
    private List<KanbanCommentModel> comments;

    @Valid
    private List<KanbanSubTaskModel> subTasks;

    @Valid
    private List<KanbanLabelModel> labels;

    @Valid
    private List<KanbanAcceptanceCriteriaModel> acceptanceCriteria;
}
