package com.example.problems.dpk16_mosquito_game.impl1;


public enum MoveStrategy {

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
