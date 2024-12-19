package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
class DPK05_impl_10Test {

    DPK05_impl_10 dpk05Impl10;

    @BeforeEach
    void setup(){
        dpk05Impl10 = new DPK05_impl_10();
    }

    @Test
    void getPowerShouldBeSucess() {
        assertEquals(90, dpk05Impl10.getPower("Paul"));
    }

    @Test
    void getPowerfulShouldBeSucess() {
        assertEquals("Paul", dpk05Impl10.getPowerful("George", "Paul"));
    }

    @Test
    void getPowerfulDrawShouldBeSucess() {
        assertEquals(DPK05_impl_10.DRAW, dpk05Impl10.getPowerful("George", "George"));
    }

    @Test
    void getLeaderboardShouldBeSucess() {
        assertEquals("John", dpk05Impl10.play("John", "Paul"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl10.getLeaderboard());

        assertEquals("John", dpk05Impl10.play("John", "Ringo"));
        leaderboard = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5
        );
        assertEquals(leaderboard, dpk05Impl10.getLeaderboard());
    }

    @Test
    void getLeaderboardDrawShouldBeSucess() {
        assertEquals(DPK05_impl_10.DRAW, dpk05Impl10.play("Ringo", "Ringo"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 0,
                "Paul", 0,
                "George", 0,
                "Ringo", 10
        );
        assertEquals(leaderboard, dpk05Impl10.getLeaderboard());
    }


}
