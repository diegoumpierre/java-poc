package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.UnexpectedException;

import static org.junit.jupiter.api.Assertions.*;

class MoveStrategyTest {

    private Game game;

    @BeforeEach
    void setUp() {
        this.game = new Game(5, 5, 0);
    }

    @Test
    void upShouldBeSuccess() {
        //up
        assertArrayEquals(new int[]{4, 0}, MoveStrategy.UP.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{3, 0}, MoveStrategy.UP.getApplication().execute(new int[]{game.getRow() - 1, 0}, game));
    }

    @Test
    void downShouldBeSuccess() {
        //down
        assertArrayEquals(new int[]{1, 0}, MoveStrategy.DOWN.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{0, 0}, MoveStrategy.DOWN.getApplication().execute(new int[]{game.getRow() - 1, 0}, game));
    }

    @Test
    void leftShouldBeSuccess() {
        //left
        assertArrayEquals(new int[]{0, game.getColumn() - 1}, MoveStrategy.LEFT.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{0, 3}, MoveStrategy.LEFT.getApplication().execute(new int[]{0, game.getColumn() - 1}, game));
    }

    @Test
    void rightShouldBeSuccess() {
        //right
        assertArrayEquals(new int[]{0, 1}, MoveStrategy.RIGHT.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{0, 0}, MoveStrategy.RIGHT.getApplication().execute(new int[]{0, game.getColumn() - 1}, game));
    }

    @Test
    void diagonalUpRightShouldBeSuccess() {
        //diagonal - up right
        assertArrayEquals(new int[]{4, 1}, MoveStrategy.DIAGONAL_UP_RIGHT.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{3, 0}, MoveStrategy.DIAGONAL_UP_RIGHT.getApplication().execute(new int[]{game.getRow() - 1, game.getColumn() - 1}, game));
    }

    @Test
    void diagonalUpLeftShouldBeSuccess() {
        //diagonal - up left
        assertArrayEquals(new int[]{4, 4}, MoveStrategy.DIAGONAL_UP_LEFT.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{3, 3}, MoveStrategy.DIAGONAL_UP_LEFT.getApplication().execute(new int[]{game.getRow() - 1, game.getColumn() - 1}, game));
    }

    @Test
    void diagonalDownRightShouldBeSuccess() {
        //diagonal - down right
        assertArrayEquals(new int[]{1, 1}, MoveStrategy.DIAGONAL_DOWN_RIGHT.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{0, 0}, MoveStrategy.DIAGONAL_DOWN_RIGHT.getApplication().execute(new int[]{game.getRow() - 1, game.getColumn() - 1}, game));
    }

    @Test
    void diagonalDownLeftShouldBeSuccess() {
        //diagonal - down left
        assertArrayEquals(new int[]{1, 4}, MoveStrategy.DIAGONAL_DOWN_LEFT.getApplication().execute(new int[]{0, 0}, game));
        assertArrayEquals(new int[]{0, 3}, MoveStrategy.DIAGONAL_DOWN_LEFT.getApplication().execute(new int[]{game.getRow() - 1, game.getColumn() - 1}, game));
    }
}

