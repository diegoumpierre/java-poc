package com.example.problems.dpk11_replace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK11_impl_1Test {

    @Test
    void replaceStringShouldBeSuccess(){
        String expected = "Hello-World-How-Are-You";
        String input = "Hello,World,How,Are,You";
        String token = ",";
        String newToken = "-";

        assertEquals(expected, DPK11_impl_1.replace(input,token,newToken));
    }


    @Test
    void replaceString2ShouldBeSuccess(){
        String expected = "Hello-How,Are,You";
        String input = "Hello,World,How,Are,You";
        String token = ",World,";
        String newToken = "-";

        assertEquals(expected, DPK11_impl_1.replace(input,token,newToken));
    }

}