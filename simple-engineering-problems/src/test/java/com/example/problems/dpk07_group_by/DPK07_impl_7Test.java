package com.example.problems.dpk07_group_by;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DPK07_impl_7Test {

    @Test
    void groupByShouldSuccess(){
        List<List<Integer>> expected = new ArrayList<>();
        expected.add(Arrays.asList(1,2,3));
        expected.add(Arrays.asList(4,5,6));
        expected.add(Arrays.asList(7,8,9));
        expected.add(Arrays.asList(10));

        assertEquals(expected, DPK07_impl_7.group_by(Arrays.asList(1,2,3,4,5,6,7,8,9,10),3));
    }

    @Test
    void groupByStringShouldSuccess(){
        List<List<String>> expected = new ArrayList<>();
        expected.add(Arrays.asList("a","b","c"));
        expected.add(Arrays.asList("d","e","f"));
        expected.add(Arrays.asList("g","h","i"));
        expected.add(Arrays.asList("j"));

        assertEquals(expected, DPK07_impl_7.group_by(Arrays.asList("a","b","c","d","e","f","g","h","i","j"),3));
    }
}