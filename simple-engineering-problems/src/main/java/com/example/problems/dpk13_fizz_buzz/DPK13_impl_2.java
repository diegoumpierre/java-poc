package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_2 {

    private static int ZERO = 0;
    private static int THREE = 3;
    private static int FIVE = 5;

    public static Object[] fizzBuzz(int numbersToGenerate) {
        Object[] result = new Object[numbersToGenerate];
        for (int i = 1; i <= numbersToGenerate; i++) {
            if (mod(i,THREE) && mod(i,FIVE)) {
                result[i - 1] = "FizzBuzz";
                continue;
            }
            if (mod(i,THREE)) {
                result[i - 1] = "Fizz";
                continue;
            }
            if (mod(i,FIVE)) {
                result[i - 1] = "Buzz";
                continue;
            }
            result[i - 1] = i;
        }
        return result;
    }

    private static boolean mod(int number, int divisor){
        return number % divisor == ZERO;
    }

}