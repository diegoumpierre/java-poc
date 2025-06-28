package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class LasagnaTest {
    private Lasagna lasagna;
    @BeforeEach
    void init(){
        lasagna = new Lasagna();
    }

    @Test
    void task1() {
        assertEquals(40, lasagna.expectedMinutesInOven());
    }

    @Test
    void task2() {
        assertEquals(10, lasagna.remainingMinutesInOven(30));
    }

    @Test
    void task3() {
        assertEquals(4, lasagna.preparationTimeInMinutes(2));
    }

    @Test
    void task4() {
        assertEquals(26, lasagna.totalTimeInMinutes(3, 20));
    }


}