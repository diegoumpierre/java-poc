package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK05_impl_8Test {
    DPK05_impl_8 dpk05Impl8 = new DPK05_impl_8();


    @Test
    void getPowerSuccess() {
        assertEquals(100, dpk05Impl8.getPower("John"));
    }

    @Test
    void getPowerfulSuccess() {
        assertEquals("John", dpk05Impl8.getPowerful("John", "Ringo"));
    }

    @Test
    void getPowerfulDrawSuccess() {
        assertEquals(DPK05_impl_8.DRAW, dpk05Impl8.getPowerful("Ringo", "Ringo"));
    }

    @Test
    void getLeaderBoardSucess() {
        assertEquals("John", dpk05Impl8.play("John", "Paul"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl8.getLeaderboard());

        assertEquals("John", dpk05Impl8.play("John", "Ringo"));
        leaderboard = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5
        );
        assertEquals(leaderboard, dpk05Impl8.getLeaderboard());
    }

    @Test
    void getLeaderBoardDrawSucess() {
        assertEquals(DPK05_impl_9.DRAW, dpk05Impl8.play("Ringo", "Ringo"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 0,
                "Paul", 0,
                "George", 0,
                "Ringo", 10
        );
        assertEquals(leaderboard, dpk05Impl8.getLeaderboard());
    }
}