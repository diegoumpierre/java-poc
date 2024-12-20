package com.example.problems.dpk10_reduce;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class DPK10_impl_10Test {

    @Test
    void reduceShouldBeSuccess() {
        int expected = 15;
        int inital = 0;
        BinaryOperator<Integer> binaryOperator = (acc, x) -> acc + x;
        List<Integer> input = List.of(1, 2, 3, 4, 5);
        assertEquals(expected, DPK10_impl_10.reduce(input, binaryOperator, inital));
    }
}