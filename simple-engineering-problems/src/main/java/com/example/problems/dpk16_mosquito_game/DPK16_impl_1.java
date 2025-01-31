package com.example.problems.dpk16_mosquito_game;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Thread.sleep;

public class DPK16_impl_1 {


    interface MovementApplication {
        int[] execute(int[] position);
    }

    enum MoveStrategy {
        UP(new Up()),
        DOWN(new Down()),
        LEFT(new Left()),
        RIGHT(new Right()),
        DIAGONAL_UP_RIGHT(new DiagonalUpRight()),
        DIAGONAL_UP_LEFT(new DiagonalUpLeft()),
        DIAGONAL_DOWN_RIGHT(new DiagonalDownRight()),
        DIAGONAL_DOWN_LEFT(new DiagonalDownLeft());

        private final MovementApplication application;

        MoveStrategy(MovementApplication application) {
            this.application = application;
        }

        public MovementApplication getApplication() {
            return application;
        }

        private static class Up implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                if (position[0] + 1 > Game.row - 1) {
                    position[0] = 0;
                } else {
                    position[0]++;
                }
                return position;
            }
        }

        private static class Down implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                if (position[0] - 1 < 0) {
                    position[0] = Game.row - 1;
                } else {
                    position[0]--;
                }
                return position;
            }
        }

        private static class Right implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                if (position[1] + 1 > Game.column - 1) {
                    position[1] = 0;
                } else {
                    position[1]++;
                }
                return position;
            }
        }

        private static class Left implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                if (position[1] - 1 < 0) {
                    position[1] = Game.column - 1;
                } else {
                    position[1]--;
                }
                return position;
            }
        }

        private static class DiagonalUpRight implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                Up up1 = new Up();
                Right right1 = new Right();
                return right1.execute(up1.execute(position));
            }
        }

        private static class DiagonalUpLeft implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                Up up1 = new Up();
                Left left1 = new Left();
                return left1.execute(up1.execute(position));
            }
        }

        private static class DiagonalDownRight implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                Down down1 = new Down();
                Right right1 = new Right();
                return right1.execute(down1.execute(position));
            }
        }

        private static class DiagonalDownLeft implements MovementApplication {
            @Override
            public int[] execute(int[] position) {
                Down down1 = new Down();
                Left left1 = new Left();
                return left1.execute(down1.execute(position));
            }
        }
    }

    class Mosquito {
        private int round = 0;
        private int[] position;
        private Random random;
        private int moves;

        public Mosquito(Random random, int[] position) {
            this.random = random;
            this.position = position;
            this.moves = 0;
        }

        public Mosquito() {
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

        private MoveStrategy getNextMove() {
            int randomIndex = random.nextInt(MoveStrategy.values().length);
            return MoveStrategy.values()[randomIndex];
        }

        public void move() {
            MoveStrategy moveStrategy = getNextMove();
            position = moveStrategy.getApplication().execute(position);
            moves++;
        }
    }

    class Exterminator {
        private int[] position;
        private MoveStrategy direction = MoveStrategy.DIAGONAL_UP_RIGHT;

        public Exterminator(int[] position) {
            this.position = position;
        }

        public int[] getPosition() {
            return position;
        }

        public void move() {
            //top right
            if (position[0] == 99 && position[1] == 99) {
                position = MoveStrategy.UP.getApplication().execute(position);
                direction = MoveStrategy.DIAGONAL_UP_LEFT;
                return;
            }
            //top left
            if (position[0] == 99 && position[1] == 0) {
                position = MoveStrategy.UP.getApplication().execute(position);
                direction = MoveStrategy.DIAGONAL_UP_RIGHT;
                return;
            }

            position = direction.getApplication().execute(position);
        }


    }

    class Game {
        private boolean endGame = false;
        private int mosquitoAlive = 0;
        private int mosquitoKilled = 0;
        public static int row, column, mosquito, exterminator;
        private Object[][] grid;

        public Game(int row, int column, int mosquito, int exterminator) {
            Game.row = row;
            Game.column = column;
            Game.mosquito = mosquito;
            Game.exterminator = exterminator;
            grid = new Object[row][column];
        }

        public Game() {
            Game.row = 100;
            Game.column = 100;
            Game.mosquito = 10;
            Game.exterminator = 1;
            grid = new Object[Game.row][Game.column];
        }

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
                Exterminator exterminator = new Exterminator(new int[]{0, 0});
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
                Mosquito mosquito = new Mosquito(new Random(), new int[]{i, 0});
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
                                if (mosquito.moves >= 5) {
                                    //check if exists a mosquito near


                                    //need check all the position near
                                }
                                mosquito.move();
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

        private Mosquito reproduceMosquito(Mosquito mosquito) {

            Arrays.stream(MoveStrategy.values()).forEach(moveStrategy -> {

                if (null != getMosquitoFromGrid(
                        moveStrategy.getApplication().execute(mosquito.getPosition()))) {
                    createMosquito(1);
                    mosquito.moves = 0;
                    mosquitoAlive++;
                }
            });
            return mosquito;
        }

        public void run() throws InterruptedException {
            startGame();
            printMatrix();

            //percorrer a grid
            //percorrer a list mosquito e do exterminator
            //fazer distribuido
            //usar a lista para mosquitos e exterminator

            while (true) {
                sleep(1000);
                //method tick (centralized)
            }
        }

    }
}