package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record RewardDTO(
    UUID id,
    String name,
    String description,
    Integer pointsCost,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
