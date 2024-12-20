package com.example.problems.dpk10_reduce;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class DPK10_impl_5Test {

    @Test
    void reduceShouldBeSuccess(){
        int result = 15;
        int initial = 0;
        List<Integer> input = List.of(1,2,3,4,5);
        BinaryOperator<Integer> binaryOperator = (acc,x)-> acc+x;
        assertEquals(result,DPK10_impl_5.reduce(input,binaryOperator,initial));
    }

}