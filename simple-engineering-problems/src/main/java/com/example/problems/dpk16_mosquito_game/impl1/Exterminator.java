package com.example.problems.dpk16_mosquito_game.impl1;


class Exterminator {
    private int[] position;
    private MoveStrategy direction = MoveStrategy.DIAGONAL_UP_RIGHT;

    private Game game;

    public Exterminator(int[] position, Game game) {
        this.position = position;
        this.game = game;
    }

    public int[] getPosition() {
        return position;
    }

    public void move() {
        //top right
        if (position[0] == game.getRow() && position[1] == game.getColumn()) {
            position = MoveStrategy.UP.getApplication().execute(position, game);
            direction = MoveStrategy.DIAGONAL_UP_LEFT;
            return;
        }
        //top left
        if (position[0] == game.getRow() && position[1] == game.getColumn()) {
            position = MoveStrategy.UP.getApplication().execute(position, game);
            direction = MoveStrategy.DIAGONAL_UP_RIGHT;
            return;
        }

        position = direction.getApplication().execute(position, game);
    }


}
