package com.example.problems.dpk16_mosquito_game.impl2;

import java.util.Random;

public class Game {

    private int mosquitoAlive = 0;
    private int mosquitoKilled = 0;
    private Object[][] grid;

    public Game(int row, int column, int mosquito) {
        grid = new Object[row][column];
        this.mosquitoAlive = mosquito;
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

    protected void printMatrix(int round) {
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
        System.out.println("ROND= " + round + " # Mosquito alive = " + getMosquitoAlive() + " | Mosquito killed = " + mosquitoKilled);
    }


    private void tick(int round) {

        for (int i = 0; i < getRow(); i++) {
            for (int j = 0; j < getColumn(); j++) {
                Object object = grid[i][j];

                if (object instanceof Mosquito mosquito) {
                    if (mosquito.getRound() != round) {
                        //mosquito come from another round, so we need move them
                        mosquito = moveInTheGrid(mosquito, round);
                        grid[i][j] = null;

                        if (mosquito != null) {
                            mosquito.moves++;
                            grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
                        }
                    }
                }


                if (object instanceof Exterminator exterminator) {
                    if (exterminator.getRound() != round) {
                        exterminator.setRound(round);
                        exterminator.move();

                        Object possibleMosquito = grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]];
                        if (possibleMosquito instanceof Mosquito) {
                            mosquitoAlive--;
                            mosquitoKilled++;
                        }
                        grid[i][j] = null;
                        grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]] = exterminator;
                    }
                }

                //after do all the moves with the mosquito's and the exterminator - now reproduce the mosquito
                for (int k = 0; k < getRow(); k++) {
                    for (int l = 0; l < getColumn(); l++) {
                        Object object1 = grid[k][l];

                        if (object1 instanceof Mosquito mosquito) {
                            if (mosquito.getMoves() == 5 && hasMosquitoNearby(mosquito)) {
                                int[] nextFreeSpace = getGridNextFreeSpace();
                                Mosquito mosquitoChild = new Mosquito(new Random(), nextFreeSpace, getRow(), getColumn(), round);
                                grid[mosquitoChild.getPosition()[0]][mosquitoChild.getPosition()[1]] = mosquitoChild;
                                mosquito.moves = 0;
                                mosquitoAlive++;
                            }
                        }
                    }
                }
            }
        }
    }
        private int[] getGridNextFreeSpace () {
            for (int i = 0; i < getRow(); i++) {
                for (int j = 0; j < getColumn(); j++) {
                    if (grid[i][j] == null) {
                        return new int[]{i, j};
                    }
                }
            }
            throw new RuntimeException("#### ===> Don't have more space in the grid!");
        }


        public void run () {
            try {
                //at the first round we create the exterminator and the mosquito's
                int round = 0;

                //creating one exterminator
                Exterminator exterminator = new Exterminator(new int[]{getRow() - 1, 0}, getRow(), getColumn());
                grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]] = exterminator;

                //creating the mosquito's
                for (int i = 0; i < mosquitoAlive; i++) {
                    int[] nextFreeSpace = getGridNextFreeSpace();

                    Mosquito mosquito = new Mosquito(new Random(), nextFreeSpace, getRow(), getColumn(), round);

                    grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
                }


                //creating the mosquito's
                for (int i = 0; i < mosquitoAlive; i++) {
                    int[] nextFreeSpace = getGridNextFreeSpace();

                    Mosquito mosquito = new Mosquito(new Random(), nextFreeSpace, getRow(), getColumn(), round);

                    grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
                }
                printMatrix(round);

                while (mosquitoAlive > 0) {

                    round++;
                    tick(round);
                    printMatrix(round);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    public Mosquito moveInTheGrid(Mosquito mosquito, int round) {
        mosquito.setRound(round);
        mosquito.move();

        //The new position have something?
        Object itemFromTheNewPosition = grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]];

        if (itemFromTheNewPosition instanceof Exterminator exterminator) {
            mosquitoAlive--;
            mosquitoKilled++;
            return null;
        }

        if (itemFromTheNewPosition instanceof Mosquito) {
            return moveInTheGrid(mosquito, round);
        }

        return mosquito;
    }

        public boolean hasMosquitoNearby (Mosquito mosquito){
            for (MoveStrategy strategy : MoveStrategy.values()) {
                int[] positionToCheck = strategy.getApplication().execute(new int[]{mosquito.getPosition()[0], mosquito.getPosition()[1]}, getRow(), getColumn());

                Object possibleMosquito = grid[positionToCheck[0]][positionToCheck[1]];
                if (possibleMosquito instanceof Mosquito) {
                    return true;
                }
            }
            return false;
        }


    }
