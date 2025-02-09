package com.example.problems.dpk16_mosquito_game.impl1;


class Exterminator {
    private int[] position;
    private MoveStrategy direction = MoveStrategy.DIAGONAL_UP_RIGHT;

    public Exterminator(int[] position) {
        this.position = position;
    }

    public int[] getPosition() {
        return position;
    }

    public void move() {
        //top right
        if (position[0] == Game.row && position[1] == Game.column) {
            position = MoveStrategy.UP.getApplication().execute(position);
            direction = MoveStrategy.DIAGONAL_UP_LEFT;
            return;
        }
        //top left
        if (position[0] == Game.row && position[1] == Game.column) {
            position = MoveStrategy.UP.getApplication().execute(position);
            direction = MoveStrategy.DIAGONAL_UP_RIGHT;
            return;
        }

        position = direction.getApplication().execute(position);
    }


}
