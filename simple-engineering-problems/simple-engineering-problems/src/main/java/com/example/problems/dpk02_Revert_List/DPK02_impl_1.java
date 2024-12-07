package com.example.problems.dpk02_Revert_List;

public class DPK02_impl_1 {

    public static int[] revert(int[] input){
        int[] result = new int[input.length];

        for (int i = input.length-1; i >= 0 ; i--) {
            result[(input.length-1)-i] = input[i];
        }

        return result;
    }
}
