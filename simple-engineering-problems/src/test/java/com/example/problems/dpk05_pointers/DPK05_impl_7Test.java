package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DPK05_impl_7Test {

    DPK05_impl_7 dpk05Impl7 = new DPK05_impl_7();


    @Test
    void getPowerShouldBeSuccess() {
        assertEquals(100, dpk05Impl7.getPower("John"));
    }

    @Test
    void getPowerfulShouldBeSuccess() {
        assertEquals("John", dpk05Impl7.getPowerful("John", "Ringo"));
    }

    @Test
    void getPowerfulDrawShouldBeSuccess() {
        assertEquals(DPK05_impl_7.DRAW, dpk05Impl7.getPowerful("Ringo", "Ringo"));
    }

    @Test
    void getLeaderboardShouldBeSuccess() {
        assertEquals("John", dpk05Impl7.play("John", "Paul"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl7.getLeaderboard());

        assertEquals("John", dpk05Impl7.play("John", "Ringo"));
        leaderboard = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5
        );
        assertEquals(leaderboard, dpk05Impl7.getLeaderboard());
    }

    @Test
    void getLeaderboardDrawShouldBeSuccess() {
        assertEquals(DPK05_impl_7.DRAW, dpk05Impl7.play("Ringo", "Ringo"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 0,
                "Paul", 0,
                "George", 0,
                "Ringo", 10
        );
        assertEquals(leaderboard, dpk05Impl7.getLeaderboard());
    }


}