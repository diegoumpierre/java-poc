package com.poc.kanban.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateHistoryRequest {

    @NotBlank(message = "Entity type is required")
    private String entityType;

    @NotNull(message = "Entity ID is required")
    private UUID entityId;

    @NotBlank(message = "Change type is required")
    private String changeType;

    private List<CardHistoryModel.FieldChange> changes;

    private String comment;

    private Object snapshot;
}
