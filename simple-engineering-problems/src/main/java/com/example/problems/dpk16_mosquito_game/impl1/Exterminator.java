package com.example.problems.dpk16_mosquito_game.impl1;

import com.example.problems.dpk16_mosquito_game.DPK16_impl_1;

public class Exterminator {
    private int[] position;
    private DPK16_impl_1.MoveStrategy direction = DPK16_impl_1.MoveStrategy.DIAGONAL_UP_RIGHT;

    public Exterminator(int[] position) {
        this.position = position;
    }

    public int[] getPosition() {
        return position;
    }

    public void move() {
        //top right
        if (position[0] == 99 && position[1] == 99) {
            position = DPK16_impl_1.MoveStrategy.UP.getApplication().execute(position);
            direction = DPK16_impl_1.MoveStrategy.DIAGONAL_UP_LEFT;
            return;
        }
        //top left
        if (position[0] == 99 && position[1] == 0) {
            position = DPK16_impl_1.MoveStrategy.UP.getApplication().execute(position);
            direction = DPK16_impl_1.MoveStrategy.DIAGONAL_UP_RIGHT;
            return;
        }

        position = direction.getApplication().execute(position);
    }


}
