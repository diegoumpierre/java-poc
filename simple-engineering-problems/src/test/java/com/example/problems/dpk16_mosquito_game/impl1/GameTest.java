package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;

import java.rmi.UnexpectedException;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {


    @Test
    void createGameShouldBeSuccess()  {
        //given
        Game game = new Game(5, 10, 10);

        //method under test
        assertEquals(10, game.getMosquitoAlive());
        assertEquals(0, game.getMosquitoKilled());
        assertEquals(5, game.getRow());
        assertEquals(10, game.getColumn());
        game.printMatrix();
    }

    @Test
    void createGameShouldBeSuccessFullGrid() {
        //given
        Game game = new Game(1, 1, 10);

        //method under test
        assertEquals(10, game.getMosquitoAlive());
        assertEquals(0, game.getMosquitoKilled());

    }

    @Test
    void gameShouldBeSuccess() {
        //given
        Game game = new Game(5, 5, 10);
        game.run();
    }



}