package br.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DungeonGameTest {
    @Test
    public void testExampleDungeon() {
        DungeonGame game = new DungeonGame();
        int[][] dungeon = {
                {-2, -3, 3},
                {-5, -10, 1},
                {10, 30, -5}
        };
        assertEquals(7, game.calculateMinimumHP(dungeon));
    }

    @Test
    public void testSingleCellPositive() {
        DungeonGame game = new DungeonGame();
        int[][] dungeon = {
                {10}
        };
        assertEquals(1, game.calculateMinimumHP(dungeon));
    }

    @Test
    public void testSingleCellNegative() {
        DungeonGame game = new DungeonGame();
        int[][] dungeon = {
                {-10}
        };
        assertEquals(11, game.calculateMinimumHP(dungeon));
    }

    @Test
    public void testAllZeros() {
        DungeonGame game = new DungeonGame();
        int[][] dungeon = {
                {0, 0},
                {0, 0}
        };
        assertEquals(1, game.calculateMinimumHP(dungeon));
    }

    @Test
    public void testLargeNegativeAtEnd() {
        DungeonGame game = new DungeonGame();
        int[][] dungeon = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, -100}
        };
        assertEquals(101, game.calculateMinimumHP(dungeon));
    }
}