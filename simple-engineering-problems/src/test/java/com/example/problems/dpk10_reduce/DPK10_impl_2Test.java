package com.example.problems.dpk10_reduce;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class DPK10_impl_2Test {

    @Test
    void reduceShouldBeSuccess(){
        List<Integer> list = List.of(1,2,3,4,5);
        BinaryOperator<Integer> binaryOperator = (acc,x) -> acc+x;
        assertEquals(15,DPK10_impl_2.reduce(list,binaryOperator,0));

    }
}