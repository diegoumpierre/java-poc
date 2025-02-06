package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveStrategyTest {

    @Test
    void mosquitoMoveStrategyEnumShouldBeSuccess() {
        int row=100;
        int column=100;
        int mosquito=0;
        int exterminator=0;
        Game game = new Game(row, column, mosquito, exterminator);

        //up
        assertArrayEquals(new int[]{1, 0}, MoveStrategy.UP.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 0}, MoveStrategy.UP.getApplication().execute(new int[]{99, 0}));
        //down
        assertArrayEquals(new int[]{99, 0}, MoveStrategy.DOWN.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{98, 0}, MoveStrategy.DOWN.getApplication().execute(new int[]{99, 0}));
        //left
        assertArrayEquals(new int[]{0, 99}, MoveStrategy.LEFT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 98}, MoveStrategy.LEFT.getApplication().execute(new int[]{0, 99}));
        //right
        assertArrayEquals(new int[]{0, 1}, MoveStrategy.RIGHT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 0}, MoveStrategy.RIGHT.getApplication().execute(new int[]{0, 99}));
        //diagonal - up right
        assertArrayEquals(new int[]{1, 1}, MoveStrategy.DIAGONAL_UP_RIGHT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 0}, MoveStrategy.DIAGONAL_UP_RIGHT.getApplication().execute(new int[]{99, 99}));
        //diagonal - up left
        assertArrayEquals(new int[]{1, 99}, MoveStrategy.DIAGONAL_UP_LEFT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{0, 98}, MoveStrategy.DIAGONAL_UP_LEFT.getApplication().execute(new int[]{99, 99}));
        //diagonal - down right
        assertArrayEquals(new int[]{99, 1}, MoveStrategy.DIAGONAL_DOWN_RIGHT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{98, 0}, MoveStrategy.DIAGONAL_DOWN_RIGHT.getApplication().execute(new int[]{99, 99}));
        //diagonal - down left
        assertArrayEquals(new int[]{99, 99}, MoveStrategy.DIAGONAL_DOWN_LEFT.getApplication().execute(new int[]{0, 0}));
        assertArrayEquals(new int[]{98, 98}, MoveStrategy.DIAGONAL_DOWN_LEFT.getApplication().execute(new int[]{99, 99}));
    }


}