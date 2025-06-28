package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void blackjackWithKingAceSecond() {
        assertTrue(solution.isBlackjack("king", "ace"));
    }

    @Test
    public void correctParsesEight() {
        assertEquals(8, solution.parseCard("eight"));
    }

    @Test
    public void correctParsesAce() {
        assertEquals(11,solution.parseCard("ace"));
    }

    @Test
    public void correctParsesTen() {
        assertEquals(10,solution.parseCard("ten"));
    }


    @Test
    public void firstTurnWithAceKingAndDealerQueen() {
        assertEquals("S",solution.firstTurn("ace", "king", "queen"));
    }

    @Test
    public void firstTurnWithKingSevenAndDealerAce() {
        assertEquals("S",solution.firstTurn("king", "seven", "ace"));
    }
}