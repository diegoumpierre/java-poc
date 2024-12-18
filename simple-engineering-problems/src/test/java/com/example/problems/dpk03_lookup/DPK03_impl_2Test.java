package com.example.problems.dpk03_lookup;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK03_impl_2Test {

    @Test
    void findByIdShouldSuccess(){
        String expected = "John";
        assertEquals(expected,DPK03_impl_2.lookup(1));
    }

    @Test
    void getEmailByNameShouldSuccess(){
        String expected = "john@john.john.com";
        assertEquals(expected,DPK03_impl_2.lookup("John"));

    }

    @Test
    void getNameByEmailShouldSuccess(){
        assertEquals("John",DPK03_impl_2.lookup("john@john.john.com"));
    }

}