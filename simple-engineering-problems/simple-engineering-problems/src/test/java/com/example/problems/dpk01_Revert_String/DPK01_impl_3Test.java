package com.example.problems.dpk01_Revert_String;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK01_impl_3Test {

    @Test
    void nullShouldBeSuccess(){
        assertNull(DPK01_impl_3.revert(null));
    }

    @Test
    void helloShouldBeSuccess(){
        String expected = "olleh";
        assertEquals(expected, DPK01_impl_3.revert("hello"));
    }

}