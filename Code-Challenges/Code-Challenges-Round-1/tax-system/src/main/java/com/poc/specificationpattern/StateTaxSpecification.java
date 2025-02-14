package com.poc.specificationpattern;

public class StateTaxSpecification implements TaxSpecification {
    private String state;

    public StateTaxSpecification(String state) {
        this.state = state;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {
        return this.state.equals(state);
    }
}
