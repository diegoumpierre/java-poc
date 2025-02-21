package com.example.problems.dpk16_mosquito_game.impl1;

import java.util.Random;

import static java.lang.Thread.sleep;

class Game {
    private int mosquitoAlive;
    private int mosquitoKilled;
    private Object[][] grid;

    public Game(int row, int column, int mosquito) {
        grid = new Object[row][column];
        createMosquito(mosquito);
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
        return new int[]{0, 0};
    }


    private void createMosquito(int mosquitoToCreate) {
        for (int i = 0; i < mosquitoToCreate; i++) {
            int[] initialPosition = getGridNextFreeSpace();
            Mosquito mosquito = new Mosquito(new Random(), initialPosition, this);
            grid[initialPosition[0]][initialPosition[1]] = mosquito;
            mosquitoAlive++;
        }
    }

    public void printMatrix() {
        String itemToPrint;
        for (int i = 0; i < getRow(); i++) {
            for (int j = 0; j < getColumn(); j++) {
                itemToPrint = "#";
                Object object = grid[i][j];
                if (null != object && object instanceof Mosquito) {
                    itemToPrint = "M";
                }
                if (null != object && object instanceof Exterminator) {
                    itemToPrint = "E";
                }
                System.out.print(itemToPrint + "\t");  // Tab for spacing
            }
            System.out.println();
        }
        System.out.println("Mosquito alive = " + getMosquitoAlive() + " | Mosquito killed = " + mosquitoKilled);
    }

    public void run() {
        try{
            while (mosquitoAlive > 0) {
                printMatrix();
                tick();
                sleep(1000);
            }
            printMatrix();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Mosquito moveInTheGrid(Mosquito mosquito) {
        mosquito.move();
        Object possibleExterminator = grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]];
        if (null != possibleExterminator && possibleExterminator instanceof Exterminator) {
            return null;
        }
        while (possibleExterminator != null) {
            moveInTheGrid(mosquito);
        }
        return mosquito;
    }


    private void tick() {
        for (int i = 0; i < getRow(); i++) {
            for (int j = 0; j < getColumn(); j++) {
                Object object = grid[i][j];
                if (null != object && object instanceof Mosquito) {
                    Mosquito mosquito = (Mosquito) object;
                    mosquito = moveInTheGrid(mosquito);
                    if (mosquito == null) {
                        mosquitoAlive--;
                        mosquitoKilled++;
                    } else {
                        mosquito.moves++;
                        grid[i][j] = mosquito;
                    }
                    if (mosquito.moves == 5) {
                        if (mosquito.hasMosquitoNearby()) {
                            createMosquito(1);
                            mosquito.moves = 0;
                        }
                    }
                }
                if (null != object && object instanceof Exterminator) {
                    Exterminator exterminator = (Exterminator) object;
                    exterminator.move();
                    Object possibleMosquito = grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]];
                    if (null != possibleMosquito && possibleMosquito instanceof Mosquito) {
                        mosquitoAlive--;
                        mosquitoKilled++;
                    }
                    grid[i][j] = exterminator;
                }
            }
        }
    }
}