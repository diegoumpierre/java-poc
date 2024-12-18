package com.example.problems.dpk02_Revert_List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK02_impl_5Test {
    @Test
    void shouldSuccess(){
        int[] expected = { 5, 4, 3, 2, 1 };
        assertArrayEquals(expected,DPK02_impl_5.revert(new int[]{1, 2, 3, 4, 5}));
    }
}