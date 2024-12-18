package com.example.problems.dpk03_lookup;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DPK03_impl_9Test {
    @Test
    void findByIdSuccess(){
        assertEquals("John",DPK03_impl_9.lookup(1));
    }
    @Test
    void findByName(){
        assertEquals("john@john.john.com",DPK03_impl_9.lookup("John"));
    }
    @Test
    void findByEmail(){
        assertEquals("John",DPK03_impl_9.lookup("john@john.john.com"));
    }
}