package com.example.problems.dpk08_map;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK08_impl_2Test {

    @Test
    void mapShouldBeSuccess() {
        List<Integer> integerListExpected = Arrays.asList(2, 4, 6, 8, 10);
        assertEquals(integerListExpected, DPK08_impl_2.map(Arrays.asList(1, 2, 3, 4, 5), (x) -> x * 2));
    }

}
