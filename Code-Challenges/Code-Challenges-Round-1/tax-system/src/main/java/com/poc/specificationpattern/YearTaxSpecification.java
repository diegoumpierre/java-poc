package com.poc.specificationpattern;

public class YearTaxSpecification implements TaxSpecification {
    private int year;
    private double tax;

    public YearTaxSpecification(int year, double tax) {
        this.year = year;
        this.tax = tax;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {
        return this.year == year;
    }

    @Override
    public double getTax() {
        return tax;
    }
}
