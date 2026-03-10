package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ContactRequest(
    UUID memberId,
    @NotBlank(message = "Name is required")
    String name,
    String phone,
    @NotBlank(message = "Relationship is required")
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
    String notes
) {}
