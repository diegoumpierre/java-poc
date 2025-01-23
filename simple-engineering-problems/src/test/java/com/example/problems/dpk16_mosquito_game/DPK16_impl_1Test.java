package com.example.problems.dpk16_mosquito_game;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DPK16_impl_1Test {
    @Test
    void mosquitoMoveStrategyEnumShouldBeSuccess() {
        //up
        assertArrayEquals(new int[]{1, 0}, Move1Strategy.UP.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 0}, Move1Strategy.UP.getApplication().execute(new int[]{99, 0}));
        //down
        assertArrayEquals(new int[]{99, 0}, Move1Strategy.DOWN.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{98, 0}, Move1Strategy.DOWN.getApplication().execute(new int[]{99, 0}));
        //left
        assertArrayEquals(new int[]{0, 99}, Move1Strategy.LEFT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 98}, Move1Strategy.LEFT.getApplication().execute(new int[]{0, 99}));
        //right
        assertArrayEquals(new int[]{0, 1}, Move1Strategy.RIGHT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 0}, Move1Strategy.RIGHT.getApplication().execute(new int[]{0, 99}));
        //diagonal - up right
        assertArrayEquals(new int[]{1, 1}, Move1Strategy.DIAGONAL_UP_RIGHT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 0}, Move1Strategy.DIAGONAL_UP_RIGHT.getApplication().execute(new int[]{99, 99}));
        //diagonal - up left
        assertArrayEquals(new int[]{1, 99}, Move1Strategy.DIAGONAL_UP_LEFT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 98}, Move1Strategy.DIAGONAL_UP_LEFT.getApplication().execute(new int[]{99, 99}));
        //diagonal - down right
        assertArrayEquals(new int[]{99, 1}, Move1Strategy.DIAGONAL_DOWN_RIGHT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{98, 0}, Move1Strategy.DIAGONAL_DOWN_RIGHT.getApplication().execute(new int[]{99, 99}));
        //diagonal - down left
        assertArrayEquals(new int[]{99, 99}, Move1Strategy.DIAGONAL_DOWN_LEFT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{98, 98}, Move1Strategy.DIAGONAL_DOWN_LEFT.getApplication().execute(new int[]{99, 99}));
    }

    @Test
    void moveMosquitoShouldBeSuccess() {
        //give
        Random random = Mockito.mock(Random.class);
        when(random.nextInt(Move1Strategy.values().length)).thenReturn(1);
        Mosquito1 mosquito1 = new Mosquito1(random, new int[]{0, 0});
        //method under test
        mosquito1.move();
        //then
        assertArrayEquals(new int[]{99, 0}, mosquito1.getPosition());

    }

    @Test
    void moveExterminatorTopRightShouldSuccess() {
        //give
        Exterminator1 exterminator1 = new Exterminator1(new int[]{0, 0});
        //method under test
        exterminator1.move();
        //then
        assertArrayEquals(new int[]{1, 1}, exterminator1.getPosition());
    }

    @Test
    void moveExterminatorTopRightEndTopLeftShouldSuccess() {
        //give
        Exterminator1 exterminator1 = new Exterminator1(new int[]{99, 99});

        //method under test
        exterminator1.move();

        //then
        assertArrayEquals(new int[]{0, 99}, exterminator1.getPosition());
        exterminator1.move();
        assertArrayEquals(new int[]{1, 98}, exterminator1.getPosition());
        exterminator1.move();
        assertArrayEquals(new int[]{2, 97}, exterminator1.getPosition());
    }

    @Test
    void moveExterminatorTopLeftEndTopRightShouldSuccess() {
        //give
        Exterminator1 exterminator1 = new Exterminator1(new int[]{99, 0});

        //method under test
        exterminator1.move();

        //then
        assertArrayEquals(new int[]{0, 0}, exterminator1.getPosition());
        exterminator1.move();
        assertArrayEquals(new int[]{1, 1}, exterminator1.getPosition());
        exterminator1.move();
        assertArrayEquals(new int[]{2, 2}, exterminator1.getPosition());
    }

    @Test
    void mosquitoMove5TimesReproduceShouldSuccess() {
        //give
        Mosquito1 mosquito1 = new Mosquito1(new Random(), new int[]{0, 0});
        //method under test
        mosquito1.move();
        mosquito1.move();
        mosquito1.move();
        mosquito1.move();
        mosquito1.move();
        //then
        assertEquals(5, mosquito1.getMoves());
    }

    @Test
    void testPrint() {
        Game1 game1 = new Game1(10, 10, 10, 1);
        game1.run();
    }
}


/**
 * If the mosquito moves 5 times without being killed, it should reproduce if there is a mosquito nearby.
 * <p>
 * <p>
 * If the mosquito and the exterminator are in the same position, the mosquito should die.
 * <p>
 * <p>
 *
 * <p>
 * <p>
 * Every 1s the mosquito should move to a random position.
 * Every 1s the exterminator should move to a specific position.
 */