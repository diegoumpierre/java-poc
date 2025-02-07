package com.poc.taxsystem.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SCTaxTest {


    @Test
    void scTaxShouldBeSuccess() {
        SC2023Tax sc2023Tax = new SC2023Tax();
        SC2024Tax sc2024Tax = new SC2024Tax();
        SC2025Tax sc2025Tax = new SC2025Tax();
        assertEquals(new BigDecimal(34.53333).setScale(2, RoundingMode.CEILING), sc2023Tax.calculate(new BigDecimal(230.25)).setScale(2, RoundingMode.CEILING));
        assertEquals(new BigDecimal(25.00).setScale(2, RoundingMode.CEILING), sc2024Tax.calculate(new BigDecimal(100)).setScale(2, RoundingMode.CEILING));
        assertEquals(new BigDecimal(80.588).setScale(2, RoundingMode.CEILING), sc2025Tax.calculate(new BigDecimal(230.25)).setScale(2, RoundingMode.CEILING));
    }


}