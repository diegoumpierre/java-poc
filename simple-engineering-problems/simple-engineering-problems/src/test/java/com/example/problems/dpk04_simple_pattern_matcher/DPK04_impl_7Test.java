package com.example.problems.dpk04_simple_pattern_matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK04_impl_7Test {

    @Test
    void patternMatcherEnumShouldBeSuccess(){
        assertEquals("English",DPK04_impl_7.patternMatcherEnum("Usa"));
    }

    @Test
    void patternMatcherShouldBeSuccess(){
        assertEquals("English",DPK04_impl_7.patternMatcher("Usa"));
    }
}