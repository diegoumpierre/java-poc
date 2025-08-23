package br.dev;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ParsingFormattingExample {
    public static void main(String[] args) {
        String dateString = "2025-08-16";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        String formatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        System.out.println("Parsed date: " + date);
        System.out.println("Formatted date: " + formatted);
    }
}

