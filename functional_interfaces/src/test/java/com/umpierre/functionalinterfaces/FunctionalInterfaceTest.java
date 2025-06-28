package com.umpierre.functionalinterfaces;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FunctionalInterfaceTest {


    @Test
    public void functionalInterfaceMultiply(){
        ParamsProcessor<Integer> multiply = (param1, param2) -> param1 * param2;


        assertEquals(6,multiply.process(3,2));

    }

    @Test
    public void functionalInterface(){
        ParamsProcessor<Integer> minus = (param1, param2) -> param1 -  param2;
        ParamsProcessor<Integer> divide = (param1, param2) -> param1 / param2;

        assertEquals(1,minus.process(3,2));
        assertEquals(5,divide.process(10,2));
    }





}
