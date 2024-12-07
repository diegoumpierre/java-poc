package com.example.problems.dpk04_simple_pattern_matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK04_impl_9Test {

    @Test
    void usaShouldSuccess(){
        assertEquals("English",DPK04_impl_9.pattern_matcher("Usa"));
    }

    @Test
    void usaEnumShouldSuccess(){
        assertEquals("English",DPK04_impl_9.pattern_matcher_enum("Usa"));
    }
}