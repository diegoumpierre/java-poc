package com.poc.taxsystem.service;

import java.math.BigDecimal;

public interface TaxApplication {

    BigDecimal calculate(BigDecimal amount);

}
