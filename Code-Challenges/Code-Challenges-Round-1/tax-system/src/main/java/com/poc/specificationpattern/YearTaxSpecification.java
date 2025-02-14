package com.poc.specificationpattern;

public class YearTaxSpecification implements TaxSpecification {
    private int year;

    public YearTaxSpecification(int year) {
        this.year = year;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {
        return this.year == year;
    }
}
