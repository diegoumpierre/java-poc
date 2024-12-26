package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_5 {

    private static int ZERO = 0, FIVE = 5, THREE = 3;

    public static Object[] fizzBuzz(int numberToCreate) {
        Object[] result = new Object[numberToCreate];
        for (int j = 1; j <= numberToCreate; j++) {
            if (mod(j, THREE) && mod(j, FIVE)) {
                result[j - 1] = "FizzBuzz";
                continue;
            }
            if (mod(j, THREE)) {
                result[j - 1] = "Fizz";
                continue;
            }
            if (mod(j, FIVE)) {
                result[j - 1] = "Buzz";
                continue;
            }
            result[j-1] = j;
        }

        return result;
    }

    private static boolean mod(int number, int div) {
        return number % div == ZERO;
    }
}