package com.poc.taxsystem.strategy;

import java.math.BigDecimal;

public interface TaxApplication {

    BigDecimal calculate(BigDecimal amount);

}
