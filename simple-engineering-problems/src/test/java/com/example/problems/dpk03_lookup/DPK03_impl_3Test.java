package com.example.problems.dpk03_lookup;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK03_impl_3Test {

    @Test
    void findByIdShouldSuccess(){
        assertEquals("John",DPK03_impl_3.lookup(1));
    }

    @Test
    void getEmailByNameShouldSuccess(){
        assertEquals("john@john.john.com",DPK03_impl_3.lookup("John"));
    }

    @Test
    void getNameByEmailShouldSuccess(){
        assertEquals("John",DPK03_impl_3.lookup("john@john.john.com"));
    }
}