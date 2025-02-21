package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExterminatorTest {


    @Test
    void moveToRightStartFromBottomLeft() {
        //given
        Game game = new Game(5, 5, 0);
        int[] startPosition = {4, 0};
        Exterminator exterminator = new Exterminator(startPosition, game);
        //method under test
        exterminator.move();
        //them
        assertArrayEquals(new int[]{4, 1}, exterminator.getPosition());
    }

    @Test
    void moveFromBottomRight() {
        //given
        Game game = new Game(5, 5, 0);
        int[] startPosition = {4, 4};
        Exterminator exterminator = new Exterminator(startPosition, game);
        //method under test
        exterminator.move();
        //them
        assertArrayEquals(new int[]{3, 4}, exterminator.getPosition());
        //method under test
        exterminator.move();
        //them
        assertArrayEquals(new int[]{3, 3}, exterminator.getPosition());
    }

    @Test
    void moveFromLeftUpChangeDirection() {
        //given
        Game game = new Game(5, 5, 0);
        int[] startPosition = {4, 4};
        Exterminator exterminator = new Exterminator(startPosition, game);
        //method under test
        exterminator.move();
        exterminator.move();
        exterminator.move();
        exterminator.move();
        exterminator.move();
        assertArrayEquals(new int[]{3, 0}, exterminator.getPosition());
        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{2, 0}, exterminator.getPosition());
        exterminator.move();
        assertArrayEquals(new int[]{2, 1}, exterminator.getPosition());
    }

    @Test
    void moveFromRightTop() {
        //given
        Game game = new Game(5, 5, 0);
        int[] startPosition = {0, 3};
        Exterminator exterminator = new Exterminator(startPosition, game);
        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{0, 4}, exterminator.getPosition());
        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{4, 0}, exterminator.getPosition());
    }

}