package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class SolutionTest {
    private Solution solution;
    @BeforeEach
    void init(){
        solution = new Solution();
    }
    @Test
    void productionRatePerHourShouldBeSuccess() {
        assertEquals(1193.4,solution.productionRatePerHour(6));
    }
    @Test
    void workingItemsPerMinuteShouldSuccess() {
        assertEquals(19,solution.workingItemsPerMinute(6));
    }

}