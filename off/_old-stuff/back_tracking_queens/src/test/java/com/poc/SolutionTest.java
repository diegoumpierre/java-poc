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
    void allocate1queens() {
        assertTrue(solution.solutionQueen(1));
    }

    @Test
    void allocate2queens() {
        assertFalse(solution.solutionQueen(2));
    }

    @Test
    void allocate3queens() {
        assertFalse(solution.solutionQueen(3));
    }

    @Test
    void allocate4queens() {
        assertTrue(solution.solutionQueen(4));
    }

    @Test
    void allocate12queens() {
        assertTrue(solution.solutionQueen(12));
    }
}