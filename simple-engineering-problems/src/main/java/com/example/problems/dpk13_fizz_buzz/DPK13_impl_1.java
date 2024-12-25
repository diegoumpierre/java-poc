package com.example.problems.dpk13_fizz_buzz;

public class DPK13_impl_1 {


    private static final Integer ZERO =0;
    private static final Integer THREE = 3;
    private static final Integer FIVE = 5;

    public static String fizzBuzz() {
        Object n=null;
        return switch (n){
            case Integer i: {
                if (mod(i, THREE) && mod(i, FIVE)) yield "fizzbuzz";
                if (mod(i, THREE)){
                    yield "fizz";
                }
                if (mod(i, FIVE)){
                    yield "buzz";
                }
            }
            default: yield String.valueOf(n);
        };
    }

    public static Boolean mod(int number, int mod){
        return number % mod == ZERO;
    }
}
