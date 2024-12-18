package com.example.problems.dpk01_Revert_String;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK01_impl_5Test {

    @Test
    void nullSholudSuccess(){
        assertNull(DPK01_impl_5.revert(null));
    }

    @Test
    void revertHelloShouldBeSuccess(){
        assertEquals("olleh", DPK01_impl_5.revert("hello"));
    }

}