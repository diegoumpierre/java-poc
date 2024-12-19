package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK05_impl_9Test {

    DPK05_impl_9 dpk05Impl9;

    @BeforeEach
    void setup() {
        dpk05Impl9 = new DPK05_impl_9();
    }


    @Test
    void getPowerShouldBeSucess() {
        assertEquals(80, dpk05Impl9.getPower("George"));
    }

    @Test
    void getPowerfulShouldBeSucess() {
        assertEquals("John", dpk05Impl9.getPowerful("John", "Ringo"));
    }

    @Test
    void getPowerfulDrawShouldBeSucess() {
        assertEquals(DPK05_impl_9.DRAW, dpk05Impl9.getPowerful("Ringo", "Ringo"));
    }

    @Test
    void getLeaderboardShouldBeSucess() {
        assertEquals("John", dpk05Impl9.play("John", "Paul"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl9.getLeaderboard());

        assertEquals("John", dpk05Impl9.play("John", "Ringo"));
        leaderboard = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5
        );
        assertEquals(leaderboard, dpk05Impl9.getLeaderboard());
    }

    @Test
    void getLeaderboardDrawShouldBeSucess() {
        assertEquals(DPK05_impl_9.DRAW, dpk05Impl9.play("Ringo", "Ringo"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 0,
                "Paul", 0,
                "George", 0,
                "Ringo", 10
        );
        assertEquals(leaderboard, dpk05Impl9.getLeaderboard());
    }


}
