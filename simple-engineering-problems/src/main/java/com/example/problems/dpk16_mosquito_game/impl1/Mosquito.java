package com.example.problems.dpk16_mosquito_game.impl1;

import java.util.Random;

class Mosquito {
    public int round;
    private int[] position;
    private Random random;
    public int moves = 0;
    private int gameRow, gameColumn;

    public Mosquito(Random random, int[] position, int gameRow, int gameColumn, int round) {
        this.random = random;
        this.position = position;
        this.round = round;
        this.gameRow = gameRow;
        this.gameColumn = gameColumn;
    }


    public int[] getPosition() {
        return position;
    }

    public int getMoves() {
        return moves;
    }

    private MoveStrategy getNextMove() {
        int randomIndex = random.nextInt(MoveStrategy.values().length);
        return MoveStrategy.values()[randomIndex];
    }

    public void move() {
        MoveStrategy moveStrategy = getNextMove();
        this.position = moveStrategy.getApplication().execute(position, gameRow, gameColumn);
    }

}