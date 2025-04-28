package com.example.problems.dpk16_mosquito_game.impl8;

import java.util.Random;

public class Mosquito {

    private int round;
    private Random random;
    private int[] position;
    public int moves = 0;
    private int gameRow, gameColumn;

    public Mosquito(Random random, int[] position, int gameRow, int gameColumn, int round) {
        this.random = random;
        this.position = position;
        this.gameRow = gameRow;
        this.gameColumn = gameColumn;
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int[] getPosition(){
        return this.position;
    }

    public int getMoves() {
        return moves;
    }

    private MoveStrategy getNextMove(){
        int randomIndex = random.nextInt(MoveStrategy.values().length);
        return MoveStrategy.values()[randomIndex];
    }

    public void move(){
        MoveStrategy moveStrategy = getNextMove();
        this.position = moveStrategy.getApplication().execute(position, gameRow, gameColumn);
    }
}
