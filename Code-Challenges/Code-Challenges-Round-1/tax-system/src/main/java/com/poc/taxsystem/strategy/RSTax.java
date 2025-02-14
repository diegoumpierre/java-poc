package com.poc.taxsystem.strategy;

import java.math.BigDecimal;

public class RSTax {
}


class RS2023Tax implements TaxApplication {
    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.35"));
    }
}

class RS2024Tax implements TaxApplication {
    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.45"));
    }
}

class RS2025Tax implements TaxApplication {
    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.55"));
    }
}