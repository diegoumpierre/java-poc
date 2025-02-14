package com.poc.specificationpattern;

import java.util.List;

public class TaxSystemService {

    private List<TaxSpecification> taxSpecifications;

    public TaxSystemService(List<TaxSpecification> taxSpecifications) {
        this.taxSpecifications = taxSpecifications;
    }

    public double calculateTax(String state, int year, double price) {
        double taxRate = 0;
        for (TaxSpecification taxSpecification : taxSpecifications) {
            if (taxSpecification.isSatisfiedBy(state, year)) {
                taxRate += 0.5;
            }
        }
        return price * taxRate;
    }


}
