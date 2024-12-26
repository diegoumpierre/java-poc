package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_8 {


    private static int ZERO = 0, THREE = 3, FIVE = 5;

    private static boolean mod(int number, int divisor) {
        return number % divisor == ZERO;
    }

    public static Object[] fizzBuzz(int item) {
        Object[] result = new Object[item];

        for (int i = 1; i <= item; i++) {
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