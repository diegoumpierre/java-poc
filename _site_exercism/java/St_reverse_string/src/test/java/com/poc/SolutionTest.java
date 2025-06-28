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
    void case1() {
        assertEquals("ogeiD", solution.basicMethod("Diego"));
    }
    @Test
    void case2() {
        assertEquals("looc", solution.basicMethod("cool"));
    }

}