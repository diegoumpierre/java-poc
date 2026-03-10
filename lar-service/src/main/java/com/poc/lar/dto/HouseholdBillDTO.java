package com.poc.lar.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record HouseholdBillDTO(
    UUID id,
    String name,
    String category,
    Integer amountCents,
    Integer dueDay,
    String frequency,
    Boolean autoPay,
    UUID responsibleMemberId,
    String notes,
    Boolean active,
    String lastPaymentStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
