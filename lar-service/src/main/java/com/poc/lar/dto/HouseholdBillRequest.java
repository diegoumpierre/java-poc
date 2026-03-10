package com.poc.lar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record HouseholdBillRequest(
    @NotBlank(message = "Name is required")
    String name,
    String category,
    @NotNull(message = "Amount is required")
    Integer amountCents,
    Integer dueDay,
    String frequency,
    Boolean autoPay,
    UUID responsibleMemberId,
    String notes
) {}
