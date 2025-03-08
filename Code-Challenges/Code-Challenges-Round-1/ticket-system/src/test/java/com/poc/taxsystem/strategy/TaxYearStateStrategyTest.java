package com.poc.taxsystem.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaxYearStateStrategyTest {

    @Test
    void taxYearStateStrategyShouldBeSuccess(){
        assertEquals(6, TaxYearStateStrategy.values().length);

    }


}