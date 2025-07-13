package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 21 2023
 */
class SolutionTest {
    private Solution solution;
    @BeforeEach
    void init(){
        solution = new Solution();
    }
    @Test
    void case1() {
        int array[] = { 1, 2, 3, 4, 5 };
        assertFalse(solution.verifyDuplicate(array));
    }

    @Test
    void case2() {
        int array[] = { 1, 2, 3,1 };
        assertTrue(solution.verifyDuplicate(array));
    }

    @Test
    void case3() {
        int array[] = { 1, 2, 3, 4 };
        assertFalse(solution.verifyDuplicate(array));
    }
    @Test
    void case4() {
        int array[] = {1,1,1,3,3,4,3,2,4,2 };
        assertTrue(solution.verifyDuplicate(array));
    }


}