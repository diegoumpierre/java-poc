package com.poc.ticketsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Event {
    private Long id;
    private String name;
    private LocalDateTime date;
    private Venue venue;
    private int ticketsAvailable;
    private BigDecimal ticketPrice;
    private List<Zone> zones;

    // Getters e Setters
}
