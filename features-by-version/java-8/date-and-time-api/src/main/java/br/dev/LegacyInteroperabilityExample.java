package br.dev;

import java.time.Instant;
import java.util.Date;

public class LegacyInteroperabilityExample {
    public static void main(String[] args) {
        // Convert Instant to Date
        Instant instant = Instant.now();
        Date date = Date.from(instant);
        System.out.println("Date from Instant: " + date);

        // Convert Date to Instant
        Instant instantFromDate = date.toInstant();
        System.out.println("Instant from Date: " + instantFromDate);
    }
}

