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
    public void testBlackColorCode() {
        assertEquals(0,solution.colorCode("black"));
    }

    @Test
    public void testWhiteColorCode() {
        assertEquals(9,solution.colorCode("white"));
    }

    @Test
    public void testOrangeColorCode() {
        assertEquals(3,solution.colorCode("orange"));
    }

    @Test
    public void testColors() {
        String[] expected = {"black", "brown", "red", "orange", "yellow", "green", "blue", "violet", "grey", "white"};
        assertArrayEquals(expected,solution.colors());
    }
}