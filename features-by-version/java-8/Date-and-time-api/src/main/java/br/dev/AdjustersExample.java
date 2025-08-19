// Example for Adjusters in Java 8 Date and Time API
//
// TemporalAdjusters are utility methods that allow you to perform complex date manipulations
// in a readable and concise way. They are used with the 'with' method of LocalDate (and other
// temporal types) to return a new instance with the adjusted value. Common use cases include
// finding the first or last day of a month, the next or previous occurrence of a specific day
// of the week, or the first/last day of a year.
//
// All operations are immutable: they return a new LocalDate instance and do not modify the original.
//
// Example outputs (assuming today is 2025-08-16):
// First day of month: 2025-08-01
// Last day of month: 2025-08-31
// Next Monday: 2025-08-18
// Previous Friday: 2025-08-15
// First day of next year: 2026-01-01

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class AdjustersExample {
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();
        // Get the first day of the current month
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        // Get the last day of the current month
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        // Get the next Monday after today
        LocalDate nextMonday = today.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY));
        // Get the previous Friday before today
        LocalDate previousFriday = today.with(TemporalAdjusters.previous(java.time.DayOfWeek.FRIDAY));
        // Get the first day of the next year
        LocalDate firstDayOfNextYear = today.with(TemporalAdjusters.firstDayOfNextYear());
        System.out.println("First day of month: " + firstDayOfMonth);
        System.out.println("Last day of month: " + lastDayOfMonth);
        System.out.println("Next Monday: " + nextMonday);
        System.out.println("Previous Friday: " + previousFriday);
        System.out.println("First day of next year: " + firstDayOfNextYear);
    }
}
