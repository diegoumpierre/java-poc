package com.example.problems.dpk03_lookup;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK03_impl_1Test {

    @Test
    void findByIdShouldSuccess(){
        String expected = "John";
        assertEquals(expected,DPK03_impl_1.lookup(1));
    }

    @Test
    void getEmailByNameShouldSuccess(){
        String expected = "john@john.jhon.com";
        assertEquals(expected,DPK03_impl_1.lookup("John"));
    }

    @Test
    void getNameByEmailShouldSuccess(){
        String expected = "John";
        assertEquals(expected,DPK03_impl_1.lookup("john@john.jhon.com"));
    }



}