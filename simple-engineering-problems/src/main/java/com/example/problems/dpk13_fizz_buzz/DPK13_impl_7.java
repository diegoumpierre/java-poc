package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_7 {

    private static int FIVE = 5, THREE = 3, ZERO = 0;

    private static boolean mod(int number, int divisor) {
        return number % divisor == ZERO;
    }

    public static Object[] fizzBuzz(int numItems) {
        Object[] result = new Object[numItems];
        for (int i = 1; i <= numItems; i++) {
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
}