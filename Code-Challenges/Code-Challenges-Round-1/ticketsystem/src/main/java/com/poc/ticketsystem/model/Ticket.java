package com.poc.ticketsystem.model;

import java.math.BigDecimal;

public class Ticket {
    private Long id;
    private Event event;
    private Zone zone;
    private Seat seat; // Pode ser nulo se não houver assentos numerados
    private BigDecimal price;
    private User buyer;
    private TicketStatus status;
    private String qrCode;

    // Getters e Setters
}
