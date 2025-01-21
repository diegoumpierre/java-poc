package com.example.problems.dpk16_mosquito_game;


import java.util.*;

public class DPK16_impl_1 {
}

enum MosquitoMove1Enum {
    UP("UP"),
    DOWN("DOWN"),
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    DIAGONAL_UP_RIGHT("UP", "RIGHT"),
    DIAGONAL_UP_LEFT("UP", "LEFT"),
    DIAGONAL_DOWN_RIGHT("DOWN", "RIGHT"),
    DIAGONAL_DOWN_LEFT("DOWN", "LEFT");

    private List<String> movements;

    MosquitoMove1Enum(String... movements) {
        this.movements = Arrays.asList(movements);
    }

    public List<String> getMovements() {
        return movements;
    }
}

class Mosquito1 {
    private int[] position;
    private Random random;

    public int getPositionX() {
        return position[0];
    }
    public int getPositionY() {
        return position[1];
    }

    public Mosquito1(Random random, int[] position) {
        this.random = random;
        this.position = position;
    }

    public Mosquito1() {
        this.random = new Random();
        this.position = new int[]{0, 0};
    }


    private List<String> getNextMove() {
        int randomIndex = random.nextInt(MosquitoMove1Enum.values().length);
        return MosquitoMove1Enum.values()[randomIndex].getMovements();
    }

    public void mosquitoMove(int[][] gridPosition) {
        getNextMove();

    }

    private void move(int[][] grid, String movement) {
        int x = position[0];
        int y = position[1];

        switch (MosquitoMove1Enum.valueOf(movement)) {
            case UP:
                if (x + 1 > 99) {
                    x = 0;
                } else {
                    x++;
                }
                break;
            case DOWN:
                if (x - 1 < 0) {
                    x = 99;
                } else {
                    x--;
                }
                break;

            case LEFT:
                if (y - 1 < 0) {
                    y = 99;
                } else {
                    y--;
                }
                break;

            case RIGHT:
                if (y + 1 > 99) {
                    y = 0;
                } else {
                    y++;
                }
                break;
        }
    }


}
class Game1 {
    private int[][] grid = new int[100][100];

    public Game1(){

    }

}