package com.example.problems.dpk04_simple_pattern_matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK04_impl_3Test {
    @Test
    void getUsaShouldSuccess(){
        assertEquals("English",DPK04_impl_3.pattern_matcher("Usa"));
    }

    @Test
    void getUsaEnumShouldSuccess(){
        assertEquals("English",DPK04_impl_3.pattern_matcher_enum("Usa"));
    }
}