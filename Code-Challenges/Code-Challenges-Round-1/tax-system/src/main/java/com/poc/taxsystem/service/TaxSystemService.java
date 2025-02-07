package com.poc.taxsystem.service;

import java.math.BigDecimal;

public class TaxSystemService {

    public BigDecimal applyTheTaxForTheStateAndYear(String state, int year, BigDecimal amount) {
        String taxYearState = state + "_" + year;
        TaxYearStateStrategy taxYearStateStrategy = TaxYearStateStrategy.valueOf(taxYearState);
        BigDecimal finalAmount = taxYearStateStrategy.getTaxApplication().calculate(amount);

        return finalAmount;
    }


}
