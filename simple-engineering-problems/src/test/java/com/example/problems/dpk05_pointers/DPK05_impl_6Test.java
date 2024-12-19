package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DPK05_impl_6Test {

    DPK05_impl_6 dpk05Impl6 = new DPK05_impl_6();

    @Test
    void getPower(){
        assertEquals(100, dpk05Impl6.getPower("John"));
    }

    @Test
    void getPowerful(){
        assertEquals("John", dpk05Impl6.getPowerful("John","Ringo"));
    }

    @Test
    void getPowerfulDraw(){
        assertEquals(DPK05_impl_6.DRAW, dpk05Impl6.getPowerful("Ringo","Ringo"));
    }

    @Test
    void getLeaderBoard(){
        assertEquals("John", dpk05Impl6.play("John","Paul"));
        Map<String,Integer> leaderboard = Map.of(
                "John", 10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        assertEquals(leaderboard, dpk05Impl6.getLeaderboard());

        assertEquals("John", dpk05Impl6.play("John", "Ringo"));
        leaderboard = Map.of(
                "John", 20,
                "Paul", -5,
                "George", 0,
                "Ringo", -5
        );
        assertEquals(leaderboard, dpk05Impl6.getLeaderboard());
    }

    @Test
    void getLeaderBoardDraw(){
        assertEquals(DPK05_impl_6.DRAW, dpk05Impl6.play("Ringo", "Ringo"));
        Map<String,Integer> leaderboard = Map.of(
                "John", 0,
                "Paul", 0,
                "George", 0,
                "Ringo", 10
        );
        assertEquals(leaderboard, dpk05Impl6.getLeaderboard());
    }




}