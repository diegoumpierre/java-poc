package com.example.problems.dpk16_mosquito_game.impl6;




import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ExterminatorTest {

    @Test
    void moveToRightStartFromBottomLeft() {
        //given
        int[] startPosition = {4, 0};
        Exterminator exterminator = new Exterminator(startPosition, 5, 5);
        //method under test
        exterminator.move();
        //them
        assertArrayEquals(new int[]{4, 1}, exterminator.getPosition());
    }

    @Test
    void moveFromBottomRight() {
        //given
        int[] startPosition = {4, 4};
        Exterminator exterminator = new Exterminator(startPosition, 5, 5);
        //method under test
        exterminator.move();
        //them
        assertArrayEquals(new int[]{4, 0}, exterminator.getPosition());
        //method under test
        exterminator.move();
        //them
        assertArrayEquals(new int[]{4, 1}, exterminator.getPosition());
    }

    @Test
    void moveFromLeftUpChangeDirection() {
        //given
        int[] startPosition = {4, 4};
        Exterminator exterminator = new Exterminator(startPosition, 5, 5);
        //method under test
        exterminator.move();
        exterminator.move();
        exterminator.move();
        exterminator.move();
        exterminator.move();
        assertArrayEquals(new int[]{4, 4}, exterminator.getPosition());
        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{4, 0}, exterminator.getPosition());
        exterminator.move();
        assertArrayEquals(new int[]{4, 1}, exterminator.getPosition());
    }

    @Test
    void moveFromRightTop() {
        //given
        int[] startPosition = {0, 3};
        Exterminator exterminator = new Exterminator(startPosition, 5, 5);
        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{0, 4}, exterminator.getPosition());
        //method under test
        exterminator.move();
        assertArrayEquals(new int[]{4, 0}, exterminator.getPosition());
    }


}