package br.com.dojo.algoritms;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {


    private Solution solution;

    @BeforeEach
    void init(){
        solution = new Solution();
    }


    @Test
    void findHappyNumberExempleTrue() {
        assertTrue(solution.findHappyNumber(19));
    }

    @Test
    void findHappyNumberExempleFalse() {
        assertFalse(solution.findHappyNumber(2));
    }

    @Test
    void testSquareEspecial(){
        assertEquals(4,solution.squareNumberEspecial(2));
    }

    @Test
    void testSquare(){
        assertEquals(5,solution.squareNumberEspecial(12));
    }
}