package com.example.problems.dpk14_2d_walk;

import java.util.ArrayList;
import java.util.List;

public class DPK14_impl_1 {


    public static List<String> move(String[][] grid, int[] initialPos, String[] movements) {

        int x = initialPos[0];
        int y = initialPos[1];
        String valueAtActualPosition;

        List<String> excludeItems = new ArrayList<>();
        for (int i = 0; i < movements.length; i++) {
            valueAtActualPosition = grid[x][y];
            grid[x][y] = "";
            switch (movements[i]) {
                case "up":
                    if (x + 1 > 1) {
                        x = 0;
                    } else {
                        x = 1;
                    }
                    break;

                case "down":
                    if (x - 1 < 0) {
                        x = 1;
                    } else {
                        x = 0;
                    }
                    break;

                case "left":
                    if (y - 1 < 0) {
                        y = 5;
                    } else {
                        y = y - 1;
                    }
                    break;

                case "right":
                    if (y + 1 > 5) {
                        y = 0;
                    } else {
                        y = y + 1;
                    }
                    break;
            }
            if (!String.valueOf(grid[x][y]).isEmpty()) {
                excludeItems.add(grid[x][y]);
            }
            grid[x][y] = valueAtActualPosition;
        }

        return excludeItems;
    }
}
