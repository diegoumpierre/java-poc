package com.example.problems.dpk16_mosquito_game;


import java.nio.charset.StandardCharsets;
import java.util.*;

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
            if (position[0] + 1 > 99) {
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
                position[0] = 99;
            } else {
                position[0]--;
            }
            return position;
        }
    }

    private static class Right1 implements MovementApplication1 {
        @Override
        public int[] execute(int[] position) {
            if (position[1] + 1 > 99) {
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
                position[1] = 99;
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

    public Mosquito1(Random random, int[] position) {
        this.random = random;
        this.position = position;
    }

    public Mosquito1() {
        this.random = new Random();
        this.position = new int[]{0, 0};
    }


    private Move1Strategy getNextMove() {
        int randomIndex = random.nextInt(Move1Strategy.values().length);
        return Move1Strategy.values()[randomIndex];
    }

    public void mosquitoMove(int[][] gridPosition) {
        getNextMove();

    }

}


}

class Game1 {
    private int[][] grid = new int[100][100];

    public Game1() {

    }

}