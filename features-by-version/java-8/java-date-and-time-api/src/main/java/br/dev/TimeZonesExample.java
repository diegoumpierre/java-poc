package br.dev;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;

public class TimeZonesExample {
    public static void main(String[] args) {
        ZoneId tokyoZone = ZoneId.of("LosAngeles");
        ZonedDateTime tokyoTime = ZonedDateTime.now(tokyoZone);
        ZoneOffset offset = tokyoTime.getOffset();
        System.out.println("Current time in Tokyo: " + tokyoTime);
        System.out.println("Zone offset: " + offset);
    }
}

