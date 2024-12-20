package com.example.problems.dpk10_reduce;

import java.util.List;
import java.util.function.BinaryOperator;

public class DPK10_impl_3 {

    public static int reduce(List<Integer> input, BinaryOperator<Integer> binaryOperator, int initialValue) {
        int result = initialValue;
        for(Integer item : input){
            result = binaryOperator.apply(result,item);
        }
        return result;
    }
}
