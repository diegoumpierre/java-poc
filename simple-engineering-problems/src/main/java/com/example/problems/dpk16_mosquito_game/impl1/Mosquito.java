package com.example.problems.dpk16_mosquito_game.impl1;

import java.util.Random;

class Mosquito {
    public int round = 0;
    private int[] position;
    private Random random;
    public int moves;

    private Game game;


    public Mosquito(Random random, int[] position, Game game) {
        this.random = random;
        this.position = position;
        this.moves = 0;
        this.game = game;
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
        position = moveStrategy.getApplication().execute(position, game);
        moves++;
    }
}