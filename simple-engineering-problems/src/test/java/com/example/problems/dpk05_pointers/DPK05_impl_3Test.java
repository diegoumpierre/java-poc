package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DPK05_impl_3Test {

    DPK05_impl_3 dpk05Impl3 = new DPK05_impl_3();

    @Test
    void getPowerShouldBeSuccess() {
        assertEquals(100, dpk05Impl3.getPower("John"));
        assertEquals(-1, dpk05Impl3.getPower("John200"));
    }

    @Test
    void getPowerfulShouldSuccess() {
        assertEquals("John", dpk05Impl3.getPowerful("John", "Ringo"));
    }

    @Test
    void getPowerfulDrawShouldSuccess() {
        assertEquals("draw", dpk05Impl3.getPowerful("John10", "Ringo10"));
    }

    @Test
    void getLeaderboard() {
        assertEquals("John", dpk05Impl3.play("John", "Paul"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl3.getLeaderboard());

        //round 2
        assertEquals("John", dpk05Impl3.play("John", "Ringo"));
        leaderboard = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5
        );
        assertEquals(leaderboard, dpk05Impl3.getLeaderboard());
    }

    @Test
    void getLeaderboardDraw() {
        assertEquals("draw", dpk05Impl3.play("John", "John"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", 0,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl3.getLeaderboard());
    }


}