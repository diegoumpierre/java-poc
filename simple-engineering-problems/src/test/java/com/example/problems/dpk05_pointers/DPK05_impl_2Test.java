package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DPK05_impl_2Test {

    DPK05_impl_2 dpk05Impl2 = new DPK05_impl_2();

    @Test
    void getPowerGivenPersonShouldBeSuccess(){
        assertEquals(100, dpk05Impl2.getPower("John"));
        assertEquals(90, dpk05Impl2.getPower("Paul"));
        assertEquals(80, dpk05Impl2.getPower("George"));
        assertEquals(70, dpk05Impl2.getPower("Ringo"));
    }

    @Test
    void getPowerfulShouldBeSuccess(){
        assertEquals("George", dpk05Impl2.powerful("George", "Ringo"));
    }

    @Test
    void getPowerfulDrawShouldBeSuccess(){
        assertEquals("draw", dpk05Impl2.powerful("George1", "Ringo1"));
    }


    @Test
    void updateLeaderboardShouldBeSuccess(){

        //round 1
        assertEquals("John", dpk05Impl2.play("John","Paul"));
        Map<String,Integer> leaderboard = Map.of(
        "John",10,
        "Paul",-5,
        "George",0,
        "Ringo",0
        );
        assertEquals(leaderboard,dpk05Impl2.getLeaderboard());

        //round 2
        assertEquals("John", dpk05Impl2.play("John","Ringo"));
        leaderboard = Map.of(
                "John",20,
                "Paul",-5,
                "George",0,
                "Ringo",-5
        );
        assertEquals(leaderboard,dpk05Impl2.getLeaderboard());

        //round 3 - draw
        assertEquals("draw", dpk05Impl2.play("Paul","Paul"));
        leaderboard = Map.of(
                "John",20,
                "Paul",5,
                "George",0,
                "Ringo",-5
        );
        assertEquals(leaderboard,dpk05Impl2.getLeaderboard());
    }
}