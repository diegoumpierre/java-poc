package com.example.problems.dpk06_tokenizer;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DPK06_impl_3Test {

    @Test
    void tokenizeWithCommaShouldBeSucess(){
        List<String> expected = Arrays.asList("Hello", "World", "How", "Are", "You");
        assertEquals(expected, DPK06_impl_3.tokenize("Hello,World,How,Are,You", ","));
    }

    @Test
    void tokenizeWithSpaceShouldBeSucess(){
        List<String> expected = Arrays.asList("Hello", "World", "How", "Are", "You");
        assertEquals(expected, DPK06_impl_3.tokenize("Hello World How Are You", " "));
    }

    @Test
    void tokenizeWithHifenShouldBeSucess(){
        List<String> expected = Arrays.asList("Hello", "World", "How", "Are", "You");
        assertEquals(expected, DPK06_impl_3.tokenize("Hello-World-How-Are-You", "-"));
    }


}
