package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {


    @Test
    void startShouldBeSuccess() {
        //given
        Game game = new Game(10, 10, 10, 1);

        //method under test
        assertEquals(1, game.getMosquitoAlive());

    }

}