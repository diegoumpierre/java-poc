package com.example.problems.dpk16_mosquito_game;

import java.util.*;

import static java.lang.Thread.sleep;

public class DPK16_impl_1 {
}

interface MovementApplication1 {
    int[] execute(int[] position);
}

enum Move1Strategy {
    UP(new Up1()),
    DOWN(new Down1()),
    LEFT(new Left1()),
    RIGHT(new Right1()),
    DIAGONAL_UP_RIGHT(new DiagonalUpRight1()),
    DIAGONAL_UP_LEFT(new DiagonalUpLeft1()),
    DIAGONAL_DOWN_RIGHT(new DiagonalDownRight1()),
    DIAGONAL_DOWN_LEFT(new DiagonalDownLeft1());

    private final MovementApplication1 application;

    Move1Strategy(MovementApplication1 application) {
        this.application = application;
    }

    public MovementApplication1 getApplication() {
        return application;
    }

    private static class Up1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            if (position[0] + 1 > Game1.row - 1) {
                position[0] = 0;
            } else {
                position[0]++;
            }
            return position;
        }
    }

    private static class Down1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            if (position[0] - 1 < 0) {
                position[0] = Game1.row - 1;
            } else {
                position[0]--;
            }
            return position;
        }
    }

    private static class Right1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            if (position[1] + 1 > Game1.column - 1) {
                position[1] = 0;
            } else {
                position[1]++;
            }
            return position;
        }
    }

    private static class Left1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            if (position[1] - 1 < 0) {
                position[1] = Game1.column - 1;
            } else {
                position[1]--;
            }
            return position;
        }
    }

    private static class DiagonalUpRight1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            Up1 up1 = new Up1();
            Right1 right1 = new Right1();
            return right1.execute(up1.execute(position));
        }
    }

    private static class DiagonalUpLeft1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            Up1 up1 = new Up1();
            Left1 left1 = new Left1();
            return left1.execute(up1.execute(position));
        }
    }

    private static class DiagonalDownRight1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            Down1 down1 = new Down1();
            Right1 right1 = new Right1();
            return right1.execute(down1.execute(position));
        }
    }

    private static class DiagonalDownLeft1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            Down1 down1 = new Down1();
            Left1 left1 = new Left1();
            return left1.execute(down1.execute(position));
        }
    }
}

class Mosquito1 {
    private int[] position;
    private Random random;
    private int moves;

    public Mosquito1(Random random, int[] position) {
        this.random = random;
        this.position = position;
        this.moves = 0;
    }

    public Mosquito1() {
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

    private Move1Strategy getNextMove() {
        int randomIndex = random.nextInt(Move1Strategy.values().length);
        return Move1Strategy.values()[randomIndex];
    }

    public void move() {
        Move1Strategy move1Strategy = getNextMove();
        position = move1Strategy.getApplication().execute(position);
        moves++;
    }
}

class Exterminator1 {
    private int[] position;
    private Move1Strategy direction = Move1Strategy.DIAGONAL_UP_RIGHT;

    public Exterminator1(int[] position) {
        this.position = position;
    }

    public int[] getPosition() {
        return position;
    }

    public void move() {
        //top right
        if (position[0] == 99 && position[1] == 99) {
            position = Move1Strategy.UP.getApplication().execute(position);
            direction = Move1Strategy.DIAGONAL_UP_LEFT;
            return;
        }
        //top left
        if (position[0] == 99 && position[1] == 0) {
            position = Move1Strategy.UP.getApplication().execute(position);
            direction = Move1Strategy.DIAGONAL_UP_RIGHT;
            return;
        }

        position = direction.getApplication().execute(position);
    }


}

class Game1 {
    private boolean endGame = false;
    private int mosquitoAlive = 0;
    private int mosquitoKilled = 0;
    public static int row, column, mosquito, exterminator;
    private Object[][] grid;

    public Game1(int row, int column, int mosquito, int exterminator){
        Game1.row = row;
        Game1.column = column;
        Game1.mosquito = mosquito;
        Game1.exterminator = exterminator;
        grid = new Object[row][column];
    }

    public Game1(){
        Game1.row = 100;
        Game1.column = 100;
        Game1.mosquito = 10;
        Game1.exterminator = 1;
        grid = new Object[Game1.row][Game1.column];
    }

    public int getMosquitoAlive() {
        return mosquitoAlive;
    }

    public int getMosquitoKilled() {
        return mosquitoKilled;
    }

    private boolean isGridBusy(int[] position) {
        int x = position[0];
        int y = position[1];
        if (x == row) return true;
        if (y == column) return true;

        if (null != grid[x][y]) {
            return true;
        }
        return false;
    }

    public void startGame() {
        //The game should start with 1 exterminator
        for (int i = 0; i < exterminator; i++) {
            Exterminator1 exterminator1 = new Exterminator1(new int[]{0, 0});
            while (isGridBusy(exterminator1.getPosition())){
                exterminator1.move();
            }
            grid[exterminator1.getPosition()[0]][exterminator1.getPosition()[1]] = exterminator1;
        }

        //The game should start with 10 mosquito
        for (int i = 1; i <= mosquito; i++) {
            Mosquito1 mosquito1 = new Mosquito1(new Random(), new int[]{i, 0});
            while (isGridBusy(mosquito1.getPosition())){
                mosquito1.move();
            }
            grid[mosquito1.getPosition()[0]][mosquito1.getPosition()[1]] = mosquito1;
            mosquitoAlive++;
        }
    }

    private void printMatrix() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                Object object = grid[i][j];
                if (null == object) {
                    object = new String("#");
                } else if (object instanceof Mosquito1) {
                    object = new String("M");
                } else {
                    object = new String("E");
                }
                System.out.print(object + "\t");  // Tab for spacing
            }
            System.out.println();  // New line after each row
        }
        System.out.println("Mosquito alive = " + getMosquitoAlive() + " | Mosquito killed = " + mosquitoKilled);
    }

    public void run() throws InterruptedException {
        startGame();
        printMatrix();
        while (true){
            sleep(1000);

        }
    }

}
