package com.example.problems.dpk16_mosquito_game.impl9;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(new Object[5][10], game.getGrid());
    }

    @Test
    void gameShouldBeSuccess2x2Grid() {
        //given
        Game game = new Game(2, 2, 2);
        game.run();
    }

    @Test
    void gameShouldBeSuccess100x100Grid() {
        //Terrible game
        Game game = new Game(100, 100, 10);
        game.run();
    }

    @Test
    void gameShouldBeSuccess10x10Grid() {
        //Cool but the mosquito wins
        Game game = new Game(10, 10, 10);
        game.run();
    }

    @Test
    void gameShouldBeSuccess5x5Grid() {
        //Cool but the mosquito wins
        Game game = new Game(5, 5, 10);
        game.run();
    }

    @Test
    void gameShouldBeSuccess5x5Grid5mosquito() {
        //Cool but the mosquito wins
        Game game = new Game(5, 5, 5);
        game.run();
    }

  }