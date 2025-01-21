package com.example.problems.dpk16_mosquito_game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK16_impl_1Test {
    @Test
    void mosquitoMoveEnumShouldBeSuccess() {
        assertArrayEquals(List.of("UP").toArray(), MosquitoMove1Enum.UP.getMovements().toArray());
        assertArrayEquals(List.of("DOWN").toArray(), MosquitoMove1Enum.DOWN.getMovements().toArray());
        assertArrayEquals(List.of("LEFT").toArray(), MosquitoMove1Enum.LEFT.getMovements().toArray());
        assertArrayEquals(List.of("RIGHT").toArray(), MosquitoMove1Enum.RIGHT.getMovements().toArray());
        assertArrayEquals(List.of("UP", "RIGHT").toArray(), MosquitoMove1Enum.DIAGONAL_UP_RIGHT.getMovements().toArray());
        assertArrayEquals(List.of("UP", "LEFT").toArray(), MosquitoMove1Enum.DIAGONAL_UP_LEFT.getMovements().toArray());
        assertArrayEquals(List.of("DOWN", "RIGHT").toArray(), MosquitoMove1Enum.DIAGONAL_DOWN_RIGHT.getMovements().toArray());
        assertArrayEquals(List.of("DOWN", "LEFT").toArray(), MosquitoMove1Enum.DIAGONAL_DOWN_LEFT.getMovements().toArray());
    }


    @Test
    void testGrid(){
        Object[][] grid = new Object[100][100];

        //add a mosquito to grid
        Mosquito1 mosquito1 = new Mosquito1();
        grid[mosquito1.getPositionX()][mosquito1.getPositionY()] = mosquito1;

        Mosquito1 mosquito2 = new Mosquito1();
        grid[mosquito2.getPositionX()][mosquito2.getPositionY()] = mosquito2;

    }


}


/**
 *
 * Every 1s the mosquito should move to a random position.
 * Every 1s the exterminator should move to a spesific position.
 * If the mosquito and the exterminator are in the same position, the mosquito should die.
 * If the mosquito moves 5 times without being killed, it should reproduce if there is a mosquito nearby.
 * The game should have a method that can return the number of mosquitos killed.
 * The game should have a method that can return the number of mosquitos alive.
 * The mosquito can walk in any direction (up, down, left, right, diagonals).
 * The exterminator can walk in one direction, it should swap the metrixs.
 *
 *  The exterminator can walk from the bottown left corner to the top right corner
 * them from the botton to the top them return on the same route.
 *
 *
 */