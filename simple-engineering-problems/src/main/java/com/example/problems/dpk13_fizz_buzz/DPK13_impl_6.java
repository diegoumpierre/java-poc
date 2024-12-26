package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_6 {


    private static int THREE = 3, FIVE = 5, ZERO = 0;

    public static Object[] fizzBuzz(int numOfItems) {

        Object[] result = new Object[numOfItems];

        for (int i = 1; i <= numOfItems; i++) {
            int j = i - 1;
            if (mod(i, THREE) && mod(i, FIVE)) {
                result[j] = "FizzBuzz";
                continue;
            }
            if (mod(i, THREE)) {
                result[j] = "Fizz";
                continue;
            }
            if (mod(i, FIVE)) {
                result[j] = "Buzz";
                continue;
            }
            result[j] = i;
        }

        return result;
    }

    private static boolean mod(int number, int divisor) {
        return number % divisor == ZERO;
    }
}