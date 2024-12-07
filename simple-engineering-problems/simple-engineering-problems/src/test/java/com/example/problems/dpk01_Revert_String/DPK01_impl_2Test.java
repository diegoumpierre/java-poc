package com.example.problems.dpk01_Revert_String;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK01_impl_2Test {

    @Test
    void nullShouldBeSuccess() {
        assertNull(DPK01_impl_2.revert(null));
    }

    @Test
    void helloShouldBeSuccess() {
        assertEquals("olleh", DPK01_impl_2.revert("hello"));
    }
}