package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DPK05_impl_1Test {

    DPK05_impl_1 dpk05Impl1 = new DPK05_impl_1();

    @Test
    void getPowerShouldBeSuccess() {
        assertEquals(100, dpk05Impl1.getPower("John"));
        assertEquals(90, dpk05Impl1.getPower("Paul"));
        assertEquals(80, dpk05Impl1.getPower("George"));
        assertEquals(70, dpk05Impl1.getPower("Ringo"));
        assertEquals(-1, dpk05Impl1.getPower("Ringo2"));
        assertEquals(70, dpk05Impl1.getPower("Ringo3"));
    }

    @Test
    void takeThepowerfullOneShouldBeSucess() {
        assertEquals("John", dpk05Impl1.getPowerFullOne("George", "John"));
    }

    @Test
    void takeThepowerfullOneDrawShouldBeSucess() {
        assertEquals("draw", dpk05Impl1.getPowerFullOne("Ringo", "Ringo3"));
    }

    @Test
    void playWithLeaderboardShouldBeSuccess() {

        //round 1
        assertEquals("John", dpk05Impl1.play("John", "Paul"));
        Map<String, Integer> leaderboardExpected1 = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0,
                "Ringo3", 0
        );
        assertEquals(dpk05Impl1.getLeaderboard(), leaderboardExpected1);

        //round 2
        assertEquals("John", dpk05Impl1.play("John", "Ringo"));
        Map<String, Integer> leaderboardExpected2 = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5,
                "Ringo3", 0
        );
        assertEquals(dpk05Impl1.getLeaderboard(), leaderboardExpected2);
    }

    @Test
    void playDrawShouldBeSuccess() {
        assertEquals("draw", dpk05Impl1.play("Ringo", "Ringo3"));
        Map<String, Integer> leaderboardExpected3 = Map.of(
                "John", 0,
                "Paul", 0,
                "George", 0,
                "Ringo", 5,
                "Ringo3", 5
        );
        assertEquals(dpk05Impl1.getLeaderboard(), leaderboardExpected3);

    }
}
