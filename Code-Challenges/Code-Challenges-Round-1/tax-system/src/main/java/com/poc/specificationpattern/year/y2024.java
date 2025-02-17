package com.poc.specificationpattern.year;

import com.poc.specificationpattern.TaxSpecification;

public class y2024 implements TaxSpecification {

    private String state;
    private int year;

    public y2024(String state, int year){
        this.state = state;
        this.year = year;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {

        return (state.equals(state) && this.year == year);
    }

    @Override
    public double getTax() {
        return 1.2;
    }
}
