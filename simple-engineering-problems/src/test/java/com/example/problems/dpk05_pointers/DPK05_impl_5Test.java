package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DPK05_impl_5Test {

    DPK05_impl_5 dpk05Impl5 = new DPK05_impl_5();

    @Test
    void getPower(){
        assertEquals(100, dpk05Impl5.getPower("John"));
    }

    @Test
    void getPowerful() {
        assertEquals("George", dpk05Impl5.getPowerful("George", "Ringo"));
    }

    @Test
    void getPowerfulDraw() {
        assertEquals(DPK05_impl_4.DRAW, dpk05Impl5.getPowerful("George", "George"));
    }

    @Test
    void getLeaderboard() {
        assertEquals("John", dpk05Impl5.play("John", "Paul"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl5.getLeaderboard());

        assertEquals("John", dpk05Impl5.play("John", "Ringo"));
        leaderboard = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5
        );
        assertEquals(leaderboard, dpk05Impl5.getLeaderboard());
    }

    @Test
    void getLeaderboardDraw() {
        assertEquals(DPK05_impl_4.DRAW, dpk05Impl5.play("John", "John"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", 0,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl5.getLeaderboard());
    }


}