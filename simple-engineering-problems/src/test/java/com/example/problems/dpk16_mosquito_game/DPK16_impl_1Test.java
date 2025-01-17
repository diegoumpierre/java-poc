package com.example.problems.dpk16_mosquito_game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK16_impl_1Test {


    @Test
    void initialGameShouldBeSuccess(){//The game should start with 10 mosquitos, 1 exterminator.
//        Game1 game1 = new Game1();
//        assertEquals(10, game1.getMosquitos());
//        assertEquals(1, game1.getExterminators());
    }

    @Test
    void mosquitoMoveToRandomPosition(){
//        Mosquito1 mosquito1 = new Mosquito1();
//        mosquito1.move();
    }

    @Test
    void tests(){
        int[][] matrix = new int[100][100];

        matrix[100][100] = 10;
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
 * The exterminator can walk from the bottown left corner to the top right corner them from the botton to the top them return on the same route.
 *
 *
 */