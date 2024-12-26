package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_10 {

    private static int ZERO = 0, FIVE = 5, THREE = 3;

    public static Object[] fizzBuzz(int items) {
        Object[] result = new Object[items];
        for (int i = 1; i <= items; i++) {
            int pos = i - 1;
            if (mod(i, THREE) && mod(i, FIVE)) {
                result[pos] = "FizzBuzz";
                continue;
            }
            if (mod(i, THREE)) {
                result[pos] = "Fizz";
                continue;
            }
            if (mod(i, FIVE)) {
                result[pos] = "Buzz";
                continue;
            }
            result[pos] = i;
        }
        return result;
    }

    private static boolean mod(int num, int divisor) {
        return num % divisor == ZERO;
    }
}