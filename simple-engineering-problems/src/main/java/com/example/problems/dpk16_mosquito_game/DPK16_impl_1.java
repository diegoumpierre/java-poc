package com.example.problems.dpk16_mosquito_game;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DPK16_impl_1 {
}

class Exterminator1 {

}

class Mosquito1 {
    private int[] position = {0,0};

    String[] directions = {"up", "down", "left", "right", "diagonals"};

    public void move(int[][] grid) {
        int x = position[0];
        int y = position[1];

        switch (getNextMove()) {
            case "up":
                if (x + 1 > 99) {
                    x = 0;
                } else {
                    x++;
                }
                break;

            case "down":
                if (x - 1 < 0) {
                    x = 99;
                } else {
                    x--;
                }
                break;

            case "left":
                if (y - 1 < 0) {
                    y = 99;
                } else {
                    y--;
                }
                break;

            case "right":
                if (y + 1 > 99) {
                    y = 0;
                } else {
                    y++;
                }
                break;
            case "diagonals":
                //need think more in the diagonal


                if (y + 1 > 99) {
                    y = 0;
                } else {
                    y++;
                }
                break;

        }
//            if (!String.valueOf(grid[x][y]).isEmpty()) {
//                excludeItems.add(grid[x][y]);
//            }
//            grid[x][y] = valueAtActualPosition;


    }

    private String getNextMove() {
        Random random = new Random();
        int randomIndex = random.nextInt(directions.length);
        return directions[randomIndex];
    }

//}
//
//    }
//
//
//    private int getRandomNumber(int axis){
//        int randomNumber = (int) (Math.random() * 101);
//        if (randomNumber == axis){
//            getRandomNumber(axis);
//        }
//        return randomNumber;
//    }
//}
//
//class Game1 {
//    private int mosquitos;
//    private int exterminators;
//
//    private int[][] matrix = new int[100][100];
//
//
//    public Game1() {
//        this.mosquitos = 10;
//        this.exterminators = 1;
//    }
//
//    //quando inicia o jogo tenho que colocar os mosquitos na matrix
//
//
//
//    public void mosquitoMove() {
//        //random position
//    }
//
//    public int getExterminators() {
//        return exterminators;
//    }
//
//    public int getMosquitos() {
//        return mosquitos;
//    }
}