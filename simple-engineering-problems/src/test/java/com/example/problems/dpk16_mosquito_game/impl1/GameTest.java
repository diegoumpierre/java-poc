package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.rmi.UnexpectedException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
        Game game = new Game(2, 2, 2);
        game.run();
    }
    @Test
    void gameShouldBeSuccess2() {
        //given
        Game game = new Game(2, 2, 1);
        game.run();
    }


    @Test
    void moveInTheGridTest() {
        //given
        Game game = new Game(2, 2, 1);
        Random random = Mockito.mock(Random.class);
        when(random.nextInt(MoveStrategy.values().length)).thenReturn(3);
        Mosquito mosquito = new Mosquito(random, new int[]{0, 0}, game, 1);
        assertEquals(mosquito, game.moveInTheGrid(mosquito));
    }



}