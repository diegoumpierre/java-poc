package com.example.problems.dpk16_mosquito_game.impl1;


class Exterminator {
    private int[] position;
    private MoveStrategy direction = MoveStrategy.RIGHT;
    private Game game;

    public Exterminator(int[] position, Game game) {
        this.position = position;
        this.game = game;
    }

    public int[] getPosition() {
        return position;
    }

//    public MoveStrategy getDirection() {
//        return direction;
//    }

    public void move() {
        //top-right -- restart to initial position
        if (direction == MoveStrategy.RIGHT && position[1] + 1 > game.getColumn() - 1 && position[0] == 0) {
            position = MoveStrategy.RIGHT.getApplication().execute(position,game);
            position = MoveStrategy.UP.getApplication().execute(position,game);
            return;
        }

        //right corner -- go up and change direction
        if (direction == MoveStrategy.RIGHT && position[1] + 1 > game.getColumn() - 1) {
            direction = MoveStrategy.LEFT;
            position = MoveStrategy.UP.getApplication().execute(position, game);
            return;
        }

        //left corner -- go up and change direction
        if (direction == MoveStrategy.LEFT && position[1] - 1 < 0) {
            direction = MoveStrategy.RIGHT;
            position = MoveStrategy.UP.getApplication().execute(position, game);
            return;
        }

        //execute the direction set
        position = direction.getApplication().execute(position, game);
    }
}
