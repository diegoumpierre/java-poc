package com.poc.lar.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BillPaymentDTO(
    UUID id,
    UUID billId,
    LocalDate referenceMonth,
    Integer amountCents,
    String status,
    LocalDateTime paidAt,
    UUID paidBy,
    String receiptUrl,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
