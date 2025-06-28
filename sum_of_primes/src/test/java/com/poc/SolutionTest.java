package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 18 2023
 */
class SolutionTest {
    private Solution solution;
    @BeforeEach
    void init(){
        solution = new Solution();
    }

   @Test
   void testIsPrimeShouldSuccess(){
        assertTrue(solution.isPrime(3));
        assertTrue(solution.isPrime(7));
        assertTrue(solution.isPrime(11));
   }

   @Test
   void case1(){
        assertEquals(17,solution.findSolution(20));
   }

   @Test
    void case2(){
        assertEquals(101,solution.findSolution(104));
    }
}