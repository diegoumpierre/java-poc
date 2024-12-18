package com.example.problems.dpk01_Revert_String;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK01_impl_8Test {


    @Test
    void nullShouldSuccess(){
        assertNull(DPK01_impl_8.revert(null));
    }

    @Test
    void helloShouldSuccess(){
        assertEquals("olleh",DPK01_impl_8.revert("hello"));
    }

}