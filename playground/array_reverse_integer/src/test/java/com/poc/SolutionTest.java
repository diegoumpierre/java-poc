package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
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
        List<Integer> result = new ArrayList<>(Arrays.asList(5,4,3,2,1));
        List<Integer> input = new ArrayList<>(Arrays.asList(1,2,3,4,5));
        assertArrayEquals(result.toArray(), solution.reverseArray(input).toArray());
    }
    @Test
    void case2() {
        List<Integer> result = new ArrayList<>(Arrays.asList(2,3,4,1));
        List<Integer> input = new ArrayList<>(Arrays.asList(1,4,3,2));
        assertArrayEquals(result.toArray(), solution.reverseArray(input).toArray());
    }

}