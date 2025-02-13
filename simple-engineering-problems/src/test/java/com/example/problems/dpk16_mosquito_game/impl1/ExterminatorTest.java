package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExterminatorTest {

    @Test
    void moveExterminatorTopRightShouldSuccess() {
        //give
        Game game = new Game(100, 100, 0, 0);

        Exterminator exterminator1 = new Exterminator(new int[]{0, 0}, game);
        //method under test
        exterminator1.move();
        //then
        assertArrayEquals(new int[]{1, 1}, exterminator1.getPosition());
    }
    @Test
    void moveExterminatorTopRightEndTopLeftShouldSuccess() {
        //give
        Game game = new Game(100, 100, 0, 0);
        Exterminator exterminator1 = new Exterminator(new int[]{99, 99}, game);

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
        Game game = new Game(100, 100, 0, 0);
        Exterminator exterminator1 = new Exterminator(new int[]{99, 0}, game);
        //method under test
        exterminator1.move();
        //then
        assertArrayEquals(new int[]{0, 0}, exterminator1.getPosition());
        exterminator1.move();
        assertArrayEquals(new int[]{1, 1}, exterminator1.getPosition());
        exterminator1.move();
        assertArrayEquals(new int[]{2, 2}, exterminator1.getPosition());
    }
    
    
}