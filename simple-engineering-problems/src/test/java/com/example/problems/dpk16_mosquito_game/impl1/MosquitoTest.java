package com.example.problems.dpk16_mosquito_game.impl1;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MosquitoTest {

    @Test
    void moveMosquitoShouldBeSuccess() {
        //give
        int row=100;
        int column=100;
        int mosquito=0;
        int exterminator=0;
        Game game = new Game(row, column, mosquito, exterminator);

        Random random = Mockito.mock(Random.class);
        when(random.nextInt(MoveStrategy.values().length)).thenReturn(1);
        Mosquito mosquito1 = new Mosquito(random, new int[]{0, 0});
        //method under test
        mosquito1.move();
        //then
        assertArrayEquals(new int[]{99, 0}, mosquito1.getPosition());
    }

}