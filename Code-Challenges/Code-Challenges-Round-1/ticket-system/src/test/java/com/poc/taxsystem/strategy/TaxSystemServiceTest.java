package com.poc.taxsystem.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class TaxSystemServiceTest {

    @Test
    void taxSystemServiceShouldBeSuccess() {
        TaxSystemService taxSystemService = new TaxSystemService();
        assertEquals(new BigDecimal(55.00).setScale(2, RoundingMode.CEILING),
                taxSystemService.applyTheTaxForTheStateAndYear("RS",2025, new BigDecimal(100.00)).setScale(2, RoundingMode.CEILING));
    }


}