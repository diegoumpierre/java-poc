package com.example.problems.dpk09_filter;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK09_impl_8Test {

    @Test
    void filterShouldBeSuccess() {
        List<Integer> expected = List.of(2, 4, 6, 8, 10);
        List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Predicate<Integer> predicate = (x) -> x % 2 == 0;
        assertEquals(expected,DPK09_impl_8.filter(input,predicate));
    }
}