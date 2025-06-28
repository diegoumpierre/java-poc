package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Jan 03 2024
 */
class BadgeTest {
    private Badge badge;
    @BeforeEach
    void init(){
        badge = new Badge();
    }

    @Test
    void printBadgeForAnEmployee(){
        String expected = "[734] - Ernest Johnny Payne - STRATEGIC COMMUNICATION";
        assertEquals(expected, badge.print(734, "Ernest Johnny Payne", "Strategic Communication"));
    }

    @Test
    void printABadgeForNewEmployee(){
        String expected ="Jane Johnson - PROCUREMENT";
        assertEquals(expected,badge.print(null, "Jane Johnson", "Procurement"));
    }

    @Test
    void printABadgeFotTheOwner(){
        String expected = "[254] - Charlotte Hale - OWNER";
        assertEquals(expected,badge.print(254, "Charlotte Hale", null));
    }

    @Test
    void printABadgeFotTheOwnerNewEmployee(){
        String expected = "Charlotte Hale - OWNER";
        assertEquals(expected,badge.print(null, "Charlotte Hale", null));
    }

}