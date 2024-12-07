package com.example.problems.dpk04_simple_pattern_matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK04_impl_8Test {

    @Test
    void patternMatcherUsaShouldBeSuccess(){
        assertEquals("English",DPK04_impl_8.patternMatcher("Usa"));
    }

    @Test
    void patternMatcherEnumUsaShouldBeSuccess(){
        assertEquals("English",DPK04_impl_8.patternMatcherEnum("Usa"));
    }

}