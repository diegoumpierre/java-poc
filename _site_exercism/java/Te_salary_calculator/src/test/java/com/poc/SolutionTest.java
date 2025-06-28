package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Oct 09 2023
 */
class SolutionTest {
    private Solution solution;
    @BeforeEach
    void init(){
        solution = new Solution();
    }

    @Test
    void daysSkipped3(){
        assertEquals(1,solution.multiplierPerDaysSkipped(3));
    }

    @Test
    void daysSkipped7(){
        assertEquals(0.85d,solution.multiplierPerDaysSkipped(7));
    }

    @Test
    void multiplierPerProductsSold21(){
        assertEquals(13,solution.multiplierPerProductsSold(21));
    }

    @Test
    void bonusForProductSold5(){
        assertEquals(50,solution.bonusForProductSold(5));
    }


    @Test
    void finalSalary(){
        int daysSkipped = 2;
        int productsSold = 3;
        assertEquals(1030,solution.finalSalary(daysSkipped, productsSold));
    }

}