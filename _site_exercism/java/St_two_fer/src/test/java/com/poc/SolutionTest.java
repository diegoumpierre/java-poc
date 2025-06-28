package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 26 2023
 */
class SolutionTest {
    private Solution solution;
    @BeforeEach
    void init(){
        solution = new Solution();
    }


    @Test
    public void noNameGiven() {
        assertEquals("One for you, one for me.",solution.twoFerOne(null));
    }

    @Test
    public void aNameGiven() {
        assertEquals("One for Alice, one for me.",solution.twoFerOne("Alice"));
    }

    @Test
    public void anotherNameGiven() {
        assertEquals("One for Bob, one for me.",solution.twoFerOne("Bob"));
    }


}