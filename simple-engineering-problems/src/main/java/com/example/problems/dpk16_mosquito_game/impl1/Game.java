package com.example.problems.dpk16_mosquito_game.impl1;

import java.util.Random;

import static java.lang.Thread.sleep;

class Game {
    private boolean endGame = false;
    private int mosquitoAlive = 0;
    private int mosquitoKilled = 0;
    private int row, column, mosquito, exterminator;
    private Object[][] grid;

    public Game(int row, int column, int mosquito, int exterminator) {
        this.row = row;
        this.column = column;
        this.mosquito = mosquito;
        this.exterminator = exterminator;
        grid = new Object[row][column];
    }

//    public Game() {
//        this.row = 100;
//        this.column = 100;
//        this.mosquito = 10;
//        this.exterminator = 1;
//        grid = new Object[this.row][this.column];
//    }

    public int getMosquitoAlive() {
        return mosquitoAlive;
    }

    public int getMosquitoKilled() {
        return mosquitoKilled;
    }


    private boolean isInvalidPosition(int[] position) {
        if (position[0] >= row || position[0] >= column) {
            return true;
        }
        return false;
    }

    private boolean isGridBusy(int[] position) {
        if (null != grid[position[0]][position[1]]) {
            return true;
        }
        return false;
    }

    public void startGame() {
        //The game should start with 1 exterminator
        for (int i = 0; i < exterminator; i++) {
            Exterminator exterminator = new Exterminator(new int[]{0, 0}, this);
            while (isInvalidPosition(exterminator.getPosition()) || isGridBusy(exterminator.getPosition())) {
                exterminator.move();
            }
            grid[exterminator.getPosition()[0]][exterminator.getPosition()[1]] = exterminator;
        }

        //The game should start with 10 mosquito
        createMosquito(mosquito);

    }

    private Mosquito getMosquitoFromGrid(int[] position) {
        Object o = grid[position[0]][position[1]];
        if (o instanceof Mosquito) {
            return (Mosquito) o;
        }
        return null;
    }

    private Exterminator getExterminatorFromGrid(int[] position) {
        Object o = grid[position[0]][position[1]];
        if (o instanceof Exterminator) {
            return (Exterminator) o;
        }
        return null;
    }

    private void printMatrix() {
        String itemToPrint;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                itemToPrint = "#";
                if (null != getMosquitoFromGrid(new int[]{i, j})) {
                    itemToPrint = "M";
                }
                if (null != getExterminatorFromGrid(new int[]{i, j})) {
                    itemToPrint = "E";
                }
                System.out.print(itemToPrint + "\t");  // Tab for spacing
            }
            System.out.println();
        }
        System.out.println("Mosquito alive = " + getMosquitoAlive() + " | Mosquito killed = " + mosquitoKilled);
    }

    private void createMosquito(int numMosquitosToCreate) {
        for (int i = 1; i <= numMosquitosToCreate; i++) {
            Mosquito mosquito = new Mosquito(new Random(), new int[]{i, 0}, null);
            while (isInvalidPosition(mosquito.getPosition()) || isGridBusy(mosquito.getPosition())) {
                mosquito.move();
            }
            grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
            mosquitoAlive++;
        }
    }

    private void tick(int round) {
        //reading the grid
        boolean mosquitoDie;
        Mosquito mosquito;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (!isGridBusy(new int[]{i, j})) {
                    continue;
                }
                //moving the mosquito
                mosquitoDie = false;
                mosquito = getMosquitoFromGrid(new int[]{i, j});
                if (null != mosquito) {
                    if (mosquito.round == round) continue;
                    int[] currentPosition = mosquito.getPosition();
                    mosquito.move();
                    while (isGridBusy(mosquito.getPosition())) {
                        if (null != getExterminatorFromGrid(mosquito.getPosition())) {
                            //exist an exterminator in the next position, so mosquito die
                            mosquitoAlive--;
                            mosquitoKilled++;
                            grid[currentPosition[0]][currentPosition[1]] = null;
                            mosquitoDie = true;
                            break;
                        } else {
                            if (mosquito.moves >= 5 && existsMosquitoNear(mosquito.getPosition())) {
                                createMosquito(1);
                                mosquitoAlive++;
                                //need check all the position near
                            } else {
                                mosquito.move();
                            }
                        }
                    }
                    if (!mosquitoDie) {
                        mosquito.round = round;
                        grid[mosquito.getPosition()[0]][mosquito.getPosition()[1]] = mosquito;
                        grid[currentPosition[0]][currentPosition[1]] = null;
                    }
                }
            }
        }
    }

    private boolean existsMosquitoNear(int[] initialPosition) {
        for (MoveStrategy moveStrategy : MoveStrategy.values()) {
            int[] newPosition = moveStrategy.getApplication().execute(initialPosition, null);
            if (null != getMosquitoFromGrid(newPosition)) {
                return true;
            }
        }

        return false;
    }

    public void run() throws InterruptedException {
        startGame();
        printMatrix();
        Random random = new Random();
        //percorrer a grid
        //percorrer a list mosquito e do exterminator
        //fazer distribuido
        //usar a lista para mosquitos e exterminator

        while (true) {
            sleep(1000);
            tick(random.nextInt());
            printMatrix();
        }
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }
}