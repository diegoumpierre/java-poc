package com.example.problems.dpk09_filter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK09_impl_3Test {

    @Test
    void filterShouldBeSuccess() {
        List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        assertEquals(expected, DPK09_impl_3.filter(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), (x) -> x % 2 == 0));
    }
}