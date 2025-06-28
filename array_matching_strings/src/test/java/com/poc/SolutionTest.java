package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

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
        List<String> stringList = new ArrayList<>(Arrays.asList( "ab","ab","abc"));
        List<String> queries = new ArrayList<>(Arrays.asList("ab","abc","bc"));
        List<Integer> expectedResult = new ArrayList<>(Arrays.asList(2, 1, 0));

        assertArrayEquals(expectedResult.toArray(),solution.matchingStrings(stringList,queries).toArray());
    }


    @Test
    void case2() {
        List<String> stringList = new ArrayList<>(Arrays.asList( "diego","diego","jonh","diego","fred"));
        List<String> queries = new ArrayList<>(Arrays.asList("diego","fred","maria"));
        List<Integer> expectedResult = new ArrayList<>(Arrays.asList(3, 1, 0));

        assertArrayEquals(expectedResult.toArray(),solution.matchingStrings(stringList,queries).toArray());

    }

}