package com.example.problems.dpk01_Revert_String;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK01_impl_4Test {

    @Test
    void nullShouldBeSuccess(){
        assertNull(DPK01_impl_4.revert(null));
    }

    @Test
    void helloShouldSuccess(){
        assertEquals("olleH", DPK01_impl_4.revert("Hello"));
    }

}