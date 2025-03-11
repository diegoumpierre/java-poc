package com.example.problems.dpk16_mosquito_game.impl1;


class Exterminator {
    public int round;
    private int[] position;
    private MoveStrategy direction = MoveStrategy.RIGHT;
    private int gameRow, gameColumn;

    public Exterminator(int[] position, int gameRow, int gameColumn) {
        this.position = position;
        this.gameRow = gameRow;
        this.gameColumn = gameColumn;
    }

    public int[] getPosition() {
        return position;
    }

    public void move() {
        //top-right -- restart to initial position
        if (direction == MoveStrategy.RIGHT && position[1] + 1 > gameColumn - 1 && position[0] == 0) {
            position = MoveStrategy.RIGHT.getApplication().execute(position, gameRow, gameColumn);
            position = MoveStrategy.UP.getApplication().execute(position, gameRow, gameColumn);
            return;
        }

        //right corner -- go up and change direction
        if (direction == MoveStrategy.RIGHT && position[1] + 1 > gameColumn - 1) {
            direction = MoveStrategy.LEFT;
            position = MoveStrategy.UP.getApplication().execute(position, gameRow, gameColumn);
            return;
        }

        //left corner -- go up and change direction
        if (direction == MoveStrategy.LEFT && position[1] - 1 < 0) {
            direction = MoveStrategy.RIGHT;
            position = MoveStrategy.UP.getApplication().execute(position, gameRow, gameColumn);
            return;
        }

        //execute the direction set
        position = direction.getApplication().execute(position, gameRow, gameColumn);
    }


}
