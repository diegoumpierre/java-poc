package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DPK05_impl_4Test {

    DPK05_impl_4 dpk05Impl4 = new DPK05_impl_4();

    @Test
    void getPower(){
        assertEquals(70, dpk05Impl4.getPower("Ringo"));
    }

    @Test
    void getPowerful(){
        assertEquals("George", dpk05Impl4.getPowerful("George","Ringo"));
    }

    @Test
    void getPowerfulDraw(){
        assertEquals("draw", dpk05Impl4.getPowerful("George","George"));
    }

    @Test
    void getLeaderboard(){
        assertEquals("John",dpk05Impl4.play("John","Paul"));
        Map<String, Integer> leaderboard = Map.of(
                "John",10,
                "Paul",-5,
                "George",0,
                "Ringo",0
        );
        assertEquals(leaderboard,dpk05Impl4.getLeaderboard());

        assertEquals("John",dpk05Impl4.play("John","Ringo"));
        leaderboard = Map.of(
                "John",20,
                "Paul",-5,
                "George",0,
                "Ringo",-5
        );
        assertEquals(leaderboard,dpk05Impl4.getLeaderboard());
    }

    @Test
    void getLeaderboardDraw() {
        DPK05_impl_4 dpk05_impl_4 = new DPK05_impl_4();
        assertEquals(DPK05_impl_4.DRAW, dpk05_impl_4.play("John", "John"));
        Map<String, Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", 0,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05_impl_4.getLeaderboard());
    }

    }