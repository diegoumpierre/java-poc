package com.poc.lar.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BillPaymentRequest(
    @NotNull(message = "Reference month is required")
    LocalDate referenceMonth,
    @NotNull(message = "Amount is required")
    Integer amountCents,
    String receiptUrl,
    String notes
) {}
