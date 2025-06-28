package com.poc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class SolutionTest {

    @Test
    void canFastAttack()
    {
        assertTrue(Solution.canFastAttack(false));
        assertFalse(Solution.canFastAttack(true));
    }

    @Test
    void canSpy()
    {
        assertTrue(Solution.canSpy(false,true,false));
        assertFalse(Solution.canSpy(false,false,false));
    }

    @Test
    void canSignalPrisoner()
    {
        assertTrue(Solution.canSignalPrisoner(false,true));
        assertFalse(Solution.canSignalPrisoner(true,false));
    }

    @Test
    void canFreePrisoner() {
        assertTrue(Solution.canFreePrisoner(false, false, false, true));
        assertTrue(Solution.canFreePrisoner(false, false, true, false));

    }


}