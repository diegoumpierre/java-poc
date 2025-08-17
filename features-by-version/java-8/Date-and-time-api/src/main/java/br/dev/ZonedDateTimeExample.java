// Example for ZonedDateTime in Java 8 Date and Time API

import java.time.ZonedDateTime;
import java.time.ZoneId;

public class ZonedDateTimeExample {
    public static void main(String[] args) {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Europe/Paris"));
        System.out.println("ZonedDateTime: " + zonedDateTime);
    }
}

