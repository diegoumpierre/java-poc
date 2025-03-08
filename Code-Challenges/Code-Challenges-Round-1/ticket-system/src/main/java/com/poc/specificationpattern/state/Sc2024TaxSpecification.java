package com.poc.specificationpattern.state;

import com.poc.specificationpattern.TaxSpecification;

public class Sc2024TaxSpecification implements TaxSpecification {

    private String state;
    private int year;

    public Sc2024TaxSpecification(String state, int year){
        this.state = state;
        this.year = year;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {

        return (state.equals(state) && this.year == year);
    }

    @Override
    public double getTax() {
        return 1.3;
    }
}
