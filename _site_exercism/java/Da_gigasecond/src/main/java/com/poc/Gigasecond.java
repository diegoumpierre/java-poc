package com.poc;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Gigasecond {
    private Long gigasecond = 1000000000L;

    private LocalDateTime dateTime;

    public Gigasecond(LocalDate moment) {
        this.dateTime = moment.atStartOfDay();
        this.dateTime = this.dateTime.plusSeconds(gigasecond);
    }

    public Gigasecond(LocalDateTime moment) {
        this.dateTime = moment.plusSeconds(gigasecond);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}