package com.example.problems.dpk10_reduce;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class DPK10_impl_3Test {

    @Test
    void reduceShouldBeSuccess(){
        int expected = 15;
        List<Integer> input = List.of(1,2,3,4,5);
        BinaryOperator<Integer> binaryOperator = (acc,x)-> acc+x;
        int initialValue= 0;
        assertEquals(expected,DPK10_impl_3.reduce(input,binaryOperator,initialValue));
    }
}