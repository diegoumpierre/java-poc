package com.poc.kanban.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanListModel {

    private UUID id;

    @Size(max = 50, message = "List ID must be at most 50 characters")
    private String listId;

    @NotBlank(message = "List title is required")
    @Size(min = 1, max = 100, message = "List title must be between 1 and 100 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N}\\s\\-_.,!?()]+$", message = "List title contains invalid characters")
    private String title;

    @Min(value = 0, message = "Position must be non-negative")
    private Integer position;

    @Valid
    private List<KanbanCardModel> cards;
}
