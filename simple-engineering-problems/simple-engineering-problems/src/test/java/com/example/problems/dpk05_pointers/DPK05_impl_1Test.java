package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class DPK05_impl_1Test {

    DPK05_impl_1 dpk05Impl1 = new DPK05_impl_1();

    @Test
    void getPowerShouldBeSuccess(){

        assertEquals(100, dpk05Impl1.getPower("John"));
        assertEquals(90, dpk05Impl1.getPower("Paul"));
        assertEquals(80, dpk05Impl1.getPower("George"));
        assertEquals(70, dpk05Impl1.getPower("Ringo"));
        assertEquals(-1, dpk05Impl1.getPower("Diego"));
    }

    @Test
    void takeThepowerfullOneShouldBeSucess(){
        assertEquals("John", dpk05Impl1.getPowerFullOne("George","John"));
    }

    @Test
    void playWithLeaderboardShouldBeSuccess(){

        assertEquals("John", dpk05Impl1.play("John", "Paul"));
        Map<String, Integer> leaderboardExpected1 = Map.of(
                "John",10,
                "Paul", -5,
                "George", 0,
                "Ringo", 0
        );
        //assertThat(dpk05Impl1.getLeaderboard(), is(leaderboardExpected1));


    }

}