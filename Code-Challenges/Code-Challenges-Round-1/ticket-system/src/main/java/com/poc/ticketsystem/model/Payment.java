package com.poc.ticketsystem.model;

import com.poc.ticketsystem.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Long id;
    private User user;
    private Ticket ticket;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;


}

