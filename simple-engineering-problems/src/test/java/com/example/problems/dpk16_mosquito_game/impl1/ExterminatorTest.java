package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExterminatorTest {
    @Test
    void moveTopLeftEndShouldSuccess() {
        //give
        Game game = new Game(10, 10, 0, 0);
        Exterminator exterminator = new Exterminator(new int[]{game.getRow(), 0}, game);

        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{0, 0}, exterminator.getPosition());
        assertEquals(MoveStrategy.DIAGONAL_UP_RIGHT, exterminator.getDirection());

        exterminator.move();
        assertArrayEquals(new int[]{1, 1}, exterminator.getPosition());
    }

    @Test
    void moveTopRightEndShouldSuccess() {
        //give
        Game game = new Game(10, 10, 0, 0);
        Exterminator exterminator = new Exterminator(new int[]{game.getRow(), game.getColumn()}, game);

        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{0, game.getColumn()}, exterminator.getPosition());
        assertEquals(MoveStrategy.DIAGONAL_UP_LEFT, exterminator.getDirection());

        exterminator.move();
        assertArrayEquals(new int[]{1, game.getColumn() - 1}, exterminator.getPosition());

    }

    @Test
    void moveDownRightEndShouldSuccess() {
        //give
        Game game = new Game(10, 10, 0, 0);
        Exterminator exterminator = new Exterminator(new int[]{0, game.getColumn()}, game);

        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{1, 0}, exterminator.getPosition());
        assertEquals(MoveStrategy.DIAGONAL_UP_RIGHT, exterminator.getDirection());
    }

    @Test
    void moveDownRightEndShouldSuccess2() {
        //give
        Game game = new Game(10, 10, 0, 0);
        Exterminator exterminator = new Exterminator(new int[]{game.getRow(), game.getColumn() - 1}, game);

        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{0, 0}, exterminator.getPosition());
        assertEquals(MoveStrategy.DIAGONAL_UP_RIGHT, exterminator.getDirection());
    }


}