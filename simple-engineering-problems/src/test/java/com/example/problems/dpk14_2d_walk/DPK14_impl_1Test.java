package com.example.problems.dpk14_2d_walk;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK14_impl_1Test {

    @Test
    void moveShouldBeSuccess() {

        String[][] grid = {
                {"Ryu", "E.Honda", "Blanka", "Guile", "Balrog", "Vega"},
                {"Ken", "Chun Li", "Zangief", "Dhalsim", "Sagat", "M.Bison"}
        };

        int[] initialPos = {0, 0};
        String[] movments = {"up", "left", "down", "right"};
        String[] expected = {"Ken", "M.Bison", "Vega"};
        String[] result = DPK14_impl_1.move(grid, initialPos, movments).toArray(new String[0]);

        assertArrayEquals(expected, result);

    }


}