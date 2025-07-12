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
        List<Integer> a = new ArrayList<>(Arrays.asList( 1,2,3));
        List<Integer> b = new ArrayList<>(Arrays.asList(3,2,1));
        List<Integer> expectedResult = new ArrayList<>(Arrays.asList(1,1));

        assertArrayEquals(expectedResult.toArray(),solution.compareTriplets(a,b).toArray());
    }

    @Test
    void case2() {
        List<Integer> a = new ArrayList<>(Arrays.asList( 5,6,7));
        List<Integer> b = new ArrayList<>(Arrays.asList(3,6,10));
        List<Integer> expectedResult = new ArrayList<>(Arrays.asList(1,1));

        assertArrayEquals(expectedResult.toArray(),solution.compareTriplets(a,b).toArray());
    }

    @Test
    void case3() {
        List<Integer> a = new ArrayList<>(Arrays.asList( 15,6,70));
        List<Integer> b = new ArrayList<>(Arrays.asList(3,6,10));
        List<Integer> expectedResult = new ArrayList<>(Arrays.asList(2,0));

        assertArrayEquals(expectedResult.toArray(),solution.compareTriplets(a,b).toArray());
    }


}