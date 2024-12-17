package com.example.problems.dpk05_pointers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK05_impl_1Test {

    DPK05_impl_1 dpk05Impl1 = new DPK05_impl_1();

    @Test
    void getPowerShouldBeSuccess(){

        assertEquals(100, dpk05Impl1.getPower("John"));
        assertEquals(90, dpk05Impl1.getPower("Paul"));
        assertEquals(80, dpk05Impl1.getPower("George"));
        assertEquals(70, dpk05Impl1.getPower("Ringo"));
    }

}