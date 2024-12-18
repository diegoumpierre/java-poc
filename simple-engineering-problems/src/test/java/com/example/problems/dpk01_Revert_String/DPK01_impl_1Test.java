package com.example.problems.dpk01_Revert_String;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK01_impl_1Test {

    private DPK01_impl_1 dpk01Impl1 = new DPK01_impl_1();


    @Test
    void revertHelloShouldBeSuccess(){
        String expected = "olleh";
        String result = dpk01Impl1.revert("hello");
        assertEquals(expected, result);
    }

    @Test
    void nullInputShouldReturnNull(){
        String expected = null;
        String result = dpk01Impl1.revert(null);
        assertEquals(expected, result);
    }

    @Test
    void bigWordShouldBeSucess(){
        String expected = "smelborp-gnireenigne";
        String result = dpk01Impl1.revert("engineering-problems");
        assertEquals(expected, result);
    }


}