package com.poc.specificationpattern;

public class StateTaxSpecification implements TaxSpecification {
    private String state;
    private double tax;

    public StateTaxSpecification(String state, double tax) {
        this.state = state;
        this.tax = tax;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {
        return this.state.equals(state);
    }

    @Override
    public double getTax() {
        return tax;
    }
}
