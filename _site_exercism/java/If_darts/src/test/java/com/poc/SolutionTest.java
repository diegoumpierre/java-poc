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

//    If the dart lands outside the target, player earns no points (0 points).
//    If the dart lands in the outer circle of the target, player earns 1 point.
//    If the dart lands in the middle circle of the target, player earns 5 points.
//    If the dart lands in the inner circle of the target, player earns 10 points.
//    The outer circle has a radius of 10 units (this is equivalent to the total radius for the entire target),
//    the middle circle a radius of 5 units, and the inner circle a radius of 1.

    @Test
    void dartLandsOutSideTheTarget() {
        assertEquals(0, solution.darts(10,10));
    }

    @Test
    void dartLandsOuterCircle() {
        assertEquals(1, solution.darts(0,10));
    }

    @Test
    void dartLandsMiddleCircle() {
        assertEquals(5, solution.darts(5,0));
    }

    @Test
    void dartLandsInnerCircle() {
        assertEquals(10, solution.darts(0,1));
    }

    @Test
    void dartLandsCenter() {
        assertEquals(10, solution.darts(0,0));
    }


}