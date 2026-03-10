package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

public record OutingRejectionRequest(
    @NotBlank(message = "Reason is required")
    String reason
) {}
