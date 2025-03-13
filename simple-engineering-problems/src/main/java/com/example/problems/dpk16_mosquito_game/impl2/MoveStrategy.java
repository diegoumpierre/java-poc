package com.example.problems.dpk16_mosquito_game.impl2;

public enum MoveStrategy {

    UP(new MoveStrategy.Up()),
    DOWN(new MoveStrategy.Down()),
    LEFT(new MoveStrategy.Left()),
    RIGHT(new MoveStrategy.Right()),
    DIAGONAL_UP_RIGHT(new MoveStrategy.DiagonalUpRight()),
    DIAGONAL_UP_LEFT(new MoveStrategy.DiagonalUpLeft()),
    DIAGONAL_DOWN_RIGHT(new MoveStrategy.DiagonalDownRight()),
    DIAGONAL_DOWN_LEFT(new MoveStrategy.DiagonalDownLeft());



    private final MovementApplication application;

    MoveStrategy(MovementApplication application) {
        this.application = application;
    }

    public MovementApplication getApplication() {
        return application;
    }

    private static class Up implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            if (position[0] - 1 < 0) {
                position[0] = gameRow - 1;
            } else {
                position[0]--;
            }
            return position;

        }
    }

    private static class Down implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            if (position[0] + 1 > gameRow - 1) {
                position[0] = 0;
            } else {
                position[0]++;
            }
            return position;
        }
    }

    private static class Left implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            if (position[1] - 1 < 0) {
                position[1] = gameColumn - 1;
            } else {
                position[1]--;
            }
            return position;
        }
    }

    private static class Right implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            if (position[1] + 1 > gameColumn - 1) {
                position[1] = 0;
            } else {
                position[1]++;
            }
            return position;
        }
    }


    private static class DiagonalUpRight implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            Up up = new MoveStrategy.Up();
            Right right = new MoveStrategy.Right();
            return right.execute(up.execute(position, gameRow, gameColumn), gameRow, gameColumn);
        }
    }

    private static class DiagonalUpLeft implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            Up up = new MoveStrategy.Up();
            Left left = new MoveStrategy.Left();
            return left.execute(up.execute(position, gameRow, gameColumn), gameRow, gameColumn);
        }
    }

    private static class DiagonalDownRight implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            Down down = new MoveStrategy.Down();
            Right right = new MoveStrategy.Right();
            return right.execute(down.execute(position, gameRow, gameColumn), gameRow, gameColumn);
        }
    }

    private static class DiagonalDownLeft implements MovementApplication {
        @Override
        public int[] execute(int[] position, int gameRow, int gameColumn) {
            MoveStrategy.Down down = new MoveStrategy.Down();
            MoveStrategy.Left left = new MoveStrategy.Left();
            return left.execute(down.execute(position, gameRow, gameColumn), gameRow, gameColumn);
        }
    }


}
