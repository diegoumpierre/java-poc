package com.example.problems.dpk16_mosquito_game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK16_impl_1Test {
    @Test
    void initialGameShouldBeSuccess(){
        Game1 game1 = new Game1();
        assertEquals(10, game1.getMosquitos());
        assertEquals(1, game1.getExterminators());
    }
}