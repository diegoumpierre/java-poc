package com.example.problems.dpk16_mosquito_game.impl3;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class MosquitoTest {


    @Test
    void moveMosquitoShouldBeSuccess() {
        //give
        Random random = Mockito.mock(Random.class);
        when(random.nextInt(MoveStrategy.values().length)).thenReturn(1);
        Mosquito mosquito = new Mosquito(random, new int[]{0, 0}, 10, 10,1);

        //method under test
        mosquito.move();

        //then
        assertArrayEquals(new int[]{1, 0}, mosquito.getPosition());
        assertEquals(0, mosquito.getMoves());
    }
    

}