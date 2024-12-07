package com.example.problems.dpk04_simple_pattern_matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK04_impl_6Test {

    @Test
    void patternMatcherShouldSuccess() {
        assertEquals("English", DPK04_impl_6.patternMatcher("Usa"));
    }

    @Test
    void patternMatcherEnumShouldSuccess() {
        assertEquals("English", DPK04_impl_6.patternMatcherEnum("Usa"));
    }

}