package br.dev;

public class SwitchExpressionExample {
    public static void main(String[] args) {
        System.out.println(dayType(DayOfWeek.MONDAY));
        System.out.println(dayType(DayOfWeek.SATURDAY));
        System.out.println(dayType(DayOfWeek.SUNDAY));
        System.out.println(dayType(null));
    }

    enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    // Example using switch expression (Java 14+; preview in Java 12/13)
    public static String dayType(DayOfWeek day) {
        return switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
            case SATURDAY, SUNDAY -> "Weekend";
            case null -> "Unknown";
        };
    }
}

