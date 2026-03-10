package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContactDTO(
    UUID id,
    UUID memberId,
    String name,
    String phone,
    String relationship,
    Integer age,
    String whereMet,
    String schoolName,
    String parentName,
    String parentPhone,
    String parent2Name,
    String parent2Phone,
    String address,
    Boolean trusted,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
