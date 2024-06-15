package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class TaxTest {
    private TaxMain solution;
    @BeforeEach
    void init(){
        solution = new TaxMain();
    }
    @Test
    void ifTheParameterAreBiggerThenTwoShouldReturnTrue() {
        assertTrue(solution.basicMethod(4));
    }
    @Test
    void ifTheParameterAreLessThenTwoShouldReturnFalse() {
        assertFalse(solution.basicMethod(1));
    }

}