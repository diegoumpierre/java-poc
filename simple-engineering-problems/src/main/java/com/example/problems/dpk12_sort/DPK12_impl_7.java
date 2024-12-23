package com.example.problems.dpk12_sort;

public class DPK12_impl_7 {


    public static int[] bubble_sort(int[] input) {
        int temp,n=input.length;
        boolean swapped;
        for (int i = 0; i < n-1; i++) {
            swapped=false;
            for (int j = 0; j < n-i-1; j++) {
                if(input[j]>input[j+1]){
                    temp=input[j+1];
                    input[j] = input[j+1];
                    input[j+1] = temp;
                    swapped=true;
                }
            }
            if(!swapped) break;
        }
        return input;
    }




}
