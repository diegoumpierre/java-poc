package com.example.problems.dpk12_sort;

public class DPK12_impl_4 {
    public static int[] bubble_sort(int[] input) {
        int n = input.length;
        int temp;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (input[j] > input[j + 1]) {
                    swapped = true;
                    temp = input[j];
                    input[j] = input[j + 1];
                    input[j + 1] = temp;
                }
            }
            if (!swapped) {
                break;
            }

        }
        return input;
    }
}
