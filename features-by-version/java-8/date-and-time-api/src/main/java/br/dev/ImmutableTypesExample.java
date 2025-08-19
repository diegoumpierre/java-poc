package br.dev;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;

public class ImmutableTypesExample {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        LocalDateTime dateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"));

        // All are immutable: operations return new instances
        LocalDate newDate = date.plusDays(5);
        System.out.println("Original date: " + date);
        System.out.println("New date: " + newDate);
    }
}

