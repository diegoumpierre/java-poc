package com.poc.helloworld;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * Here all the Tests for the Solution Class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class SolutionTest {


    private Solution solution = new Solution();

    @Test
    void helloWorldShulbBeSuccess() {
        assertEquals("Hello World in JAVA", solution.gimmeHelloWorld());
    }





}