package com.poc.kanban.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateEngineBoardRequest(
        @NotBlank(message = "Board type code is required")
        String boardTypeCode,

        @Size(max = 100, message = "Title must be at most 100 characters")
        String title,

        @Size(max = 10, message = "Prefix must be at most 10 characters")
        String prefix
) {}
