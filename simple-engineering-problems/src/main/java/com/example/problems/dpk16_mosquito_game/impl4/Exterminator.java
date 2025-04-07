package com.example.problems.dpk16_mosquito_game.impl4;


public class Exterminator {
    private int round;
    public int[] position;
    private MoveStrategy moveStrategy = MoveStrategy.RIGHT;
    private final int gameRow;
    private final int gameColumn;

    public Exterminator(int[] position, int gameRow, int gameColumn) {
        this.position = position;
        this.gameRow = gameRow;
        this.gameColumn = gameColumn;
    }


    public void setRound(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public int[] getPosition() {
        return position;
    }

    public void move() {
        if (moveStrategy == MoveStrategy.RIGHT && position[1] + 1 > gameColumn - 1 && position[0] == 0) {
            position = MoveStrategy.RIGHT.getApplication().execute(position, gameRow, gameColumn);
            position = MoveStrategy.UP.getApplication().execute(position, gameRow, gameColumn);
            return;
        }

        if (moveStrategy == MoveStrategy.LEFT && position[1] - 1 < 0) {
            moveStrategy = MoveStrategy.RIGHT;
            position = MoveStrategy.UP.getApplication().execute(position, gameRow, gameColumn);
            return;
        }

        position = moveStrategy.getApplication().execute(position, gameRow, gameColumn);

    }
}
