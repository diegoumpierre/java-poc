package com.example.problems.dpk16_mosquito_game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK16_impl_1Test {

    private DPK16_impl_1 dpk16Impl1;


    @Test
    void testMosquitoClass() throws InterruptedException {
        //give
        int row = 5;
        int column = 5;
        int mosquito = 10;
        int exterminator = 1;
        this.dpk16Impl1 = new DPK16_impl_1(row, column, mosquito, exterminator);
        DPK16_impl_1.Game game = dpk16Impl1.getGame();

        //method under test
        game.run();

        //then
        assertEquals(10, game.getMosquitoAlive());
        assertEquals(0, game.getMosquitoKilled());


    }

    //when I make all private I can't test the parts anymore

}