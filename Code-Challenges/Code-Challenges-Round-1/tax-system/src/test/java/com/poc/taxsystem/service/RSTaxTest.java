package com.poc.taxsystem.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class RSTaxTest {


    @Test
    void rsTaxShouldBeSuccess() {
        RS2023Tax rs2023Tax = new RS2023Tax();
        RS2024Tax rs2024Tax = new RS2024Tax();
        RS2025Tax rs2025Tax = new RS2025Tax();
        assertEquals(new BigDecimal(80.5875).setScale(2, RoundingMode.CEILING), rs2023Tax.calculate(new BigDecimal(230.25)).setScale(2, RoundingMode.CEILING));
        assertEquals(new BigDecimal(45.00).setScale(2, RoundingMode.CEILING), rs2024Tax.calculate(new BigDecimal(100)).setScale(2, RoundingMode.CEILING));
        assertEquals(new BigDecimal(126.63333).setScale(2, RoundingMode.CEILING), rs2025Tax.calculate(new BigDecimal(230.25)).setScale(2, RoundingMode.CEILING));
    }


}