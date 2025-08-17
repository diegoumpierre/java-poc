// Example for Period and Duration in Java 8 Date and Time API

import java.time.Period;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class PeriodDurationExample {
    public static void main(String[] args) {
        Period period = Period.ofDays(5);
        Duration duration = Duration.ofHours(2);
        LocalDate newDate = LocalDate.now().plus(period);
        LocalTime newTime = LocalTime.now().plus(duration);
        System.out.println("Period of 5 days added: " + newDate);
        System.out.println("Duration of 2 hours added: " + newTime);
    }
}

