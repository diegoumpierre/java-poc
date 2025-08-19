package br.dev;

import java.time.LocalDate;
import java.time.LocalTime;

public class LocalDateLocalTimeExample {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.of(12, 30);
        System.out.println("Today's date: " + today);
        System.out.println("Specific time: " + now);
    }
}

