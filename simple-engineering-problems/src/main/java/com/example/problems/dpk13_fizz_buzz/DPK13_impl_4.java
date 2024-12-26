package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_4 {

    private static int ZERO = 0, THREE = 3, FIVE = 5;

    public static Object[] fizzBuzz(int numbers) {
        Object[] result = new Object[numbers];

        for (int i = 1; i <= numbers; i++) {
            if (mod(i, THREE) && mod(i, FIVE)) {
                result[i - 1] = "FizzBuzz";
                continue;
            }
            if (mod(i, THREE)) {
                result[i - 1] = "Fizz";
                continue;
            }
            if (mod(i, FIVE)) {
                result[i - 1] = "Buzz";
                continue;
            }
            result[i - 1] = i;
        }

        return result;

    }

    private static boolean mod(int number, int divisor) {
        return number % divisor == ZERO;
    }

}