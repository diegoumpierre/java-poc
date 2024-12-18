package com.example.problems.dpk02_Revert_List;

public class DPK02_impl_4 {
    public static int[] revert(int[] ints) {
        int[] result = new int[ints.length];
        for (int i = 0; i < ints.length; i++) {
            result[ints.length-1-i] = ints[i];
        }

        return result;
    }
}
