package com.dojo.pascal;

public class PascalTriangle {

    public static int[][] pascalTriangle(int rows) {
        int[][] triangleResult = new int[rows][];

        for(int i = 0; i < rows; i++) {
            triangleResult[i] = new int[i + 1];
            triangleResult[i][0] = 1;
            triangleResult[i][i] = 1;

            for(int c = 1; c < i; c++){
              triangleResult[i][c] = triangleResult[i-1][c-1] + triangleResult[i-1][c];
            }
        }
        return triangleResult;
    }
}