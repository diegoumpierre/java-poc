package com.example.problems.dpk14_2d_walk;

import java.util.ArrayList;
import java.util.List;

public class DPK14_impl_5 {
    public static String[] move(String[][] grid, int[] initPos, String[] movements) {
        List<String> deleteItems = new ArrayList<>();
        int x = initPos[0], y = initPos[1];
        String currentValue;
        for (String movement : movements) {
            currentValue = grid[x][y];
            grid[x][y] = "";
            switch (movement) {
                case "up":
                    if (x + 1 > 1) {
                        x = 0;
                    } else {
                        x = x + 1;
                    }
                    break;
                case "down":
                    if (x - 1 < 0) {
                        x = 1;
                    } else {
                        x = x - 1;
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
                deleteItems.add(grid[x][y]);
            }
            grid[x][y] = currentValue;
        }
        return deleteItems.toArray(new String[0]);


    }

}