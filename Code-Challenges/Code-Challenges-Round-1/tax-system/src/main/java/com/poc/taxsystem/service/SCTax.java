package com.poc.taxsystem.service;

import java.math.BigDecimal;

class SC2023Tax implements TaxApplication {
    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.15"));
    }
}

class SC2024Tax implements TaxApplication {
    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.25"));
    }
}

class SC2025Tax implements TaxApplication {
    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.35"));
    }
}