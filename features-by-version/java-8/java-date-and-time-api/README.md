# ✅ Java 8 – Date and Time API Features (Table Format)

| #   | Feature Category          | Description                                                                                  | Example / Notes                                                                 |
|-----|---------------------------|----------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| 1.  | Immutable Types           | All date/time classes are immutable and thread-safe                                          | `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`                      |
| 2.  | LocalDate/LocalTime       | Represent date or time without timezone                                                      | `LocalDate.now()`, `LocalTime.of(12, 30)`                                       |
| 3.  | LocalDateTime             | Combines date and time, no timezone                                                          | `LocalDateTime.of(2023, 8, 16, 14, 30)`                                         |
| 4.  | ZonedDateTime             | Date and time with timezone                                                                 | `ZonedDateTime.now(ZoneId.of("Europe/Paris"))`                                  |
| 5.  | Instant                   | Machine timestamp (seconds/nanoseconds from epoch)                                           | `Instant.now()`                                                                 |
| 6.  | Period & Duration         | Represent date-based or time-based amount of time                                            | `Period.ofDays(5)`, `Duration.ofHours(2)`                                       |
| 7.  | Parsing & Formatting      | Parse and format dates/times using patterns                                                  | `DateTimeFormatter.ofPattern("yyyy-MM-dd")`                                     |
| 8.  | Adjusters                 | Manipulate dates using TemporalAdjusters                                                     | `date.with(TemporalAdjusters.firstDayOfMonth())`                                |
| 9.  | Time Zones                | Work with different time zones                                                               | `ZoneId`, `ZonedDateTime`, `ZoneOffset`                                         |
| 10. | Legacy Interoperability   | Convert between old (`Date`, `Calendar`) and new API                                        | `Date.from(instant)`, `instant.toEpochMilli()`                                  |


## 🔹 Core Features of the Date and Time API

1. **Immutable Types**
    - All main classes are immutable and thread-safe.
    - Example: `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`

2. **LocalDate/LocalTime**
    - Represent a date or time without timezone information.
    - Example: `LocalDate.now()`, `LocalTime.of(14, 30)`

3. **LocalDateTime**
    - Combines date and time, still without timezone.
    - Example: `LocalDateTime.of(2025, 8, 16, 10, 15)`

4. **ZonedDateTime**
    - Date and time with timezone.
    - Example: `ZonedDateTime.now(ZoneId.of("America/New_York"))`

5. **Instant**
    - Represents a point in time (timestamp).
    - Example: `Instant.now()`

6. **Period & Duration**
    - `Period` for date-based amounts (years, months, days).
    - `Duration` for time-based amounts (hours, minutes, seconds).
    - Example: `Period.ofMonths(1)`, `Duration.ofMinutes(90)`

7. **Parsing & Formatting**
    - Use `DateTimeFormatter` for custom patterns.
    - Example: `DateTimeFormatter.ofPattern("dd/MM/yyyy")`

8. **Adjusters**
    - Use `TemporalAdjusters` to manipulate dates.
    - Example: `date.with(TemporalAdjusters.lastDayOfMonth())`

9. **Time Zones**
    - Work with `ZoneId`, `ZoneOffset`, and `ZonedDateTime`.
    - Example: `ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))`

10. **Legacy Interoperability**
    - Convert between old and new date/time classes.
    - Example: `Date.from(instant)`, `instant.toEpochMilli()`

