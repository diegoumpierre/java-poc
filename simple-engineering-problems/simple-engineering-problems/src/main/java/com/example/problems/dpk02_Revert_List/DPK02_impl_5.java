package com.example.problems.dpk02_Revert_List;

public class DPK02_impl_5 {
    public static int[] revert(int[] source){
        int[] dest = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            dest[source.length-1-i] = source[i];
        }
        return dest;
    }
}
