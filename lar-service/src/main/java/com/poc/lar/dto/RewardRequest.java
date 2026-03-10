package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RewardRequest(
    @NotBlank(message = "Name is required")
    String name,
    String description,
    @NotNull(message = "Points cost is required")
    Integer pointsCost
) {}
