package com.example.problems.dpk12_sort;



public class DPK12_impl_10 {
    public static int[] bubble_sort(int[] input) {
        int n = input.length;
        int temp;
        boolean swap;
        for (int i = 0; i < n - 1; i++) {
            swap = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (input[j] > input[j + 1]) {
                    swap = true;
                    temp = input[j];
                    input[j] = input[j + 1];
                    input[j + 1] = temp;
                }
            }
            if (!swap) {
                break;
            }
        }
        return input;
    }
}
