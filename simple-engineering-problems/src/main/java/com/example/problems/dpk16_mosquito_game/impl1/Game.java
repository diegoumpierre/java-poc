package com.example.problems.dpk16_mosquito_game.impl1;

import java.util.Random;

import static java.lang.Thread.sleep;

class Game {
    private int mosquitoAlive;
    private int mosquitoKilled;
    private Object[][] grid;

    public Game(int row, int column, int mosquito) {
        grid = new Object[row][column];
        createMosquito(mosquito, 0);
        this.mosquitoKilled = 0;
        Exterminator exterminator = new Exterminator(new int[]{getRow() - 1, 0}, this);
        grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]] = exterminator;
    }

    public int getMosquitoAlive() {
        return mosquitoAlive;
    }

    public int getMosquitoKilled() {
        return mosquitoKilled;
    }

    public int getRow() {
        return this.grid.length;
    }

    public int getColumn() {
        return this.grid[0].length;
    }

    public Object[][] getGrid() {
        return grid;
    }

    private int[] getGridNextFreeSpace() {
        for (int i = 0; i < getRow(); i++) {
            for (int j = 0; j < getColumn(); j++) {
                if (grid[i][j] == null) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }


    private void createMosquito(int mosquitoToCreate, int round) {
        for (int i = 0; i < mosquitoToCreate; i++) {
            int[] initialPosition = getGridNextFreeSpace();
            Mosquito mosquito = new Mosquito(new Random(), initialPosition, this, round);
            grid[initialPosition[0]][initialPosition[1]] = mosquito;
            mosquitoAlive++;
        }
    }

    public void printMatrix() {
        String itemToPrint;
        for (int i = 0; i < getRow(); i++) {
            for (int j = 0; j < getColumn(); j++) {
                itemToPrint = "-";
                Object object = grid[i][j];
                if (null != object && object instanceof Mosquito) {
                    itemToPrint = "M";
                }
                if (null != object && object instanceof Exterminator) {
                    itemToPrint = "X";
                }
                System.out.print(itemToPrint + "\t");  // Tab for spacing
            }
            System.out.println();
        }
        System.out.println("Mosquito alive = " + getMosquitoAlive() + " | Mosquito killed = " + mosquitoKilled);
    }

    public void run() {
        try{
            int round = 0;
            while (mosquitoAlive > 0) {
                tick(round);
                sleep(1000);
                round++;
            }
            printMatrix();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Mosquito moveInTheGrid(Mosquito mosquito) {
        mosquito.move();
        Object itIsOccuped = grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]];
        if (null == itIsOccuped) {
            mosquito.moves++;
            grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
            return mosquito;
        }
        if (null != itIsOccuped && itIsOccuped instanceof Exterminator) {
            mosquitoAlive--;
            mosquitoKilled++;
            return null;
        }
        if (null != itIsOccuped && itIsOccuped instanceof Mosquito) {
            moveInTheGrid(mosquito);
        }
        return mosquito;
    }

//
//    Object possibleExterminator = grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]];
//                        if (null != possibleExterminator && possibleExterminator instanceof Exterminator) {
//        mosquitoAlive--;
//        mosquitoKilled++;
//    }else{
//        grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
//    }
//    grid[i][j] = null;


    private void tick(int round) {
        printMatrix();

        for (int i = 0; i < getRow(); i++) {
            for (int j = 0; j < getColumn(); j++) {
                Object object = grid[i][j];
                if (null != object && object instanceof Mosquito) {
                    Mosquito mosquito = (Mosquito) object;
                    if (mosquito.round != round) {
                        mosquito.round = round;
                         moveInTheGrid(mosquito);
                        grid[i][j] = null;
                    }
                    printMatrix();
                    if (mosquito.moves == 5) {
                        if (mosquito.hasMosquitoNearby()) {
                          //  createMosquito(1, mosquito.round+1);
                            mosquito.moves = 0;
                            grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
                        }
                    }

                    printMatrix();

                }


                if (null != object && object instanceof Exterminator && false) {
                    Exterminator exterminator = (Exterminator) object;
                    if (exterminator.round != round) {
                        exterminator.round = round;
                        exterminator.move();
                        Object possibleMosquito = grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]];
                        if (null != possibleMosquito && possibleMosquito instanceof Mosquito) {
                            mosquitoAlive--;
                            mosquitoKilled++;
                        }
                        grid[i][j] = null;
                        grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]] = exterminator;
                    }
                    printMatrix();
                }
            }
        }

    }
}