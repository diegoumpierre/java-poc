package com.example.problems.dpk08_map;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK08_impl_4Test {

    @Test
    void mapShouldSuccess() {
        List<Integer> expected = Arrays.asList(2, 4, 6, 8, 10);
        assertEquals(expected, DPK08_impl_4.map(Arrays.asList(1, 2, 3, 4, 5), (x) -> x * 2));
    }

}
