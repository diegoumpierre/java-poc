package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Solution {

   private int[][] chessboard;

   private int allocatedQueens = 0;

   public boolean basicMethod(int nQueens){

       initArrayNxN(nQueens);

       for (int column = 0; column < nQueens; column++){
           if(allocatedQueens(column,nQueens)){
               //allocated
           }
       }
       return false;
   }

   private void initArrayNxN(int nQueens){
       for (int line=0;line<nQueens;line++){
           for(int column=0;column<nQueens;column++){
               chessboard[line][column] =0;
           }
       }
   }


    private boolean allocatedQueens(int column, int nQueens) {

       if (allocatedQueens == nQueens) return true;

       for (int line=0; line < nQueens; line++) {
           if (canAllocate(line, column)) {
               chessboard[line][column] = 1;
               allocatedQueens++;
           }
       }
        return false;
    }

    protected boolean canAllocate(int line, int column) {
        if (chessboard[line][column] == 1) return false;
        return true;
    }
}