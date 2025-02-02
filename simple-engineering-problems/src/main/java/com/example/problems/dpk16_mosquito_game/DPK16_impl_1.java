package com.example.problems.dpk16_mosquito_game;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Thread.sleep;

public class DPK16_impl_1 {

    private Game game;

    public DPK16_impl_1(int row, int column, int mosquito, int exterminator) {
        this.game = new Game(row, column, mosquito, exterminator);
    }

    public Game getGame() {
        return this.game;
    }




    }
}