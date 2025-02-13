package com.example.problems.dpk16_mosquito_game.impl1;


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
        public int[] execute(int[] position, Game game) {
            if (position[0] + 1 > game.getRow() - 1) {
                position[0] = 0;
            } else {
                position[0]++;
            }
            return position;
        }
    }

    private static class Down implements MovementApplication {
        @Override
        public int[] execute(int[] position, Game game) {
            if (position[0] - 1 < 0) {
                position[0] = game.getRow() - 1;
            } else {
                position[0]--;
            }
            return position;
        }
    }

    private static class Right implements MovementApplication {
        @Override
        public int[] execute(int[] position, Game game) {
            if (position[1] + 1 > game.getColumn() - 1) {
                position[1] = 0;
            } else {
                position[1]++;
            }
            return position;
        }
    }

    private static class Left implements MovementApplication {
        @Override
        public int[] execute(int[] position, Game game) {
            if (position[1] - 1 < 0) {
                position[1] = game.getColumn() - 1;
            } else {
                position[1]--;
            }
            return position;
        }
    }

    private static class DiagonalUpRight implements MovementApplication {
        @Override
        public int[] execute(int[] position, Game game) {
            Up up1 = new Up();
            Right right1 = new Right();
            return right1.execute(up1.execute(position, game), game);
        }
    }

    private static class DiagonalUpLeft implements MovementApplication {
        @Override
        public int[] execute(int[] position, Game game) {
            Up up1 = new Up();
            Left left1 = new Left();
            return left1.execute(up1.execute(position, game), game);
        }
    }

    private static class DiagonalDownRight implements MovementApplication {
        @Override
        public int[] execute(int[] position, Game game) {
            Down down1 = new Down();
            Right right1 = new Right();
            return right1.execute(down1.execute(position, game), game);
        }
    }

    private static class DiagonalDownLeft implements MovementApplication {
        @Override
        public int[] execute(int[] position, Game game) {
            Down down1 = new Down();
            Left left1 = new Left();
            return left1.execute(down1.execute(position, game), game);
        }
    }


}
