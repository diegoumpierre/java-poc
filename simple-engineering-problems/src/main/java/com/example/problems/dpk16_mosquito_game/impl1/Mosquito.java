package com.example.problems.dpk16_mosquito_game.impl1;

import java.util.Random;

public class Mosquito {
    private int round = 0;
    private int[] position;
    private Random random;
    private int moves;

    public Mosquito(Random random, int[] position) {
        this.random = random;
        this.position = position;
        this.moves = 0;
    }

    public Mosquito() {
        this.random = new Random();
        this.position = new int[]{0, 0};
        this.moves = 0;
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
        position = moveStrategy.getApplication().execute(position);
        moves++;
    }
}