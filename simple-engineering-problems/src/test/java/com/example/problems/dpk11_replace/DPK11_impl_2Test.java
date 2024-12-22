package com.example.problems.dpk11_replace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK11_impl_2Test {

    @Test
    void replaceShouldBeSuccess(){
        String expected = "Hello-World-How-Are-You";
        String input = "Hello,World,How,Are,You";
        String token = ",";
        String newToken = "-";
        assertEquals(expected,DPK11_impl_2.replace(input,token,newToken));

    }

    @Test
    void replace2ShouldBeSuccess(){
        String expected = "Hello-How,Are,You";
        String input = "Hello,World,How,Are,You";
        String token = ",World,";
        String newToken = "-";
        assertEquals(expected,DPK11_impl_2.replace(input,token,newToken));

    }

}