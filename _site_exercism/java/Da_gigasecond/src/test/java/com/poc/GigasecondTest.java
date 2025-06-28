package com.poc;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class GigasecondTest {

    @Test
    void addGigaSecondToDate() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2043, Month.JANUARY, 1, 1, 46, 40);
        Gigasecond gigasecond = new Gigasecond(LocalDate.of(2011, Month.APRIL, 25));
        Assert.assertEquals(expectedDateTime,gigasecond.getDateTime());

    }


    @Test
    void addGigaSecondToDateEpochTime() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2009, Month.FEBRUARY, 19, 1, 46, 40);
        Gigasecond gigasecond = new Gigasecond(LocalDate.of(1977, Month.JUNE, 13));
        Assert.assertEquals(expectedDateTime,gigasecond.getDateTime());

    }

    @Test
    void addGigaSecondWithFullDate() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2046, Month.OCTOBER, 2, 23, 46, 40);
        Gigasecond gigasecond = new Gigasecond(LocalDateTime.of(2015, Month.JANUARY, 24, 22, 0, 0));
        Assert.assertEquals(expectedDateTime,gigasecond.getDateTime());

    }
}