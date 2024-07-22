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
   private int numberOfQueens;

   public boolean solutionQueen(int numberOfQueensToAllocate){
       chessboard = new int[numberOfQueensToAllocate][numberOfQueensToAllocate];
       numberOfQueens = numberOfQueensToAllocate;

       if (solution(0) == false) {
           System.out.printf("No Solution");
           return false;
       }

       printSolution();
       return true;
   }

   public boolean solution(int columnToCheck){
       //have allocatedAll
       if (allocatedQueens >= numberOfQueens) return true;

       //check all lines
       for(int row=0; row < numberOfQueens; row++){
           if(canAllocate(row, columnToCheck)){
                chessboard[row][columnToCheck] = 1;
                allocatedQueens +=1;
                if (solution(columnToCheck +1)){
                    return true;
                }else {
                    chessboard[row][columnToCheck] = 0;
                    allocatedQueens -=1;
                }
           }
       }
       return false;
   }


   public boolean canAllocate(int rowToCheck, int columToCheck){

       //check the columns before
       for(int column=0; column < columToCheck; column++){
           if(chessboard[rowToCheck][column] == 1) return false;
       }

       //check the columns after
       for(int column=columToCheck; column < numberOfQueens; column++){
           if(chessboard[rowToCheck][column] == 1) return false;
       }

       // Check upper diagonal on left side
       for (int row = rowToCheck, column = columToCheck; row >= 0 && column >= 0; row--, column--){
           if (chessboard[row][column] == 1) return false;
       }

       // Check down diagonal on left side
       for (int row = rowToCheck, column = columToCheck; row < numberOfQueens && column >= 0; row++, column--){
           if (chessboard[row][column] == 1) return false;
       }
       return true;
   }

    public void printSolution(){
        for(int row=0; row < numberOfQueens; row++){
            for (int column=0; column < numberOfQueens; column++){
                if (chessboard[row][column]==1){
                    System.out.print(" Q ");
                }else{
                    System.out.print(" . ");
                }
            }
            System.out.println("");
        }
    }
}