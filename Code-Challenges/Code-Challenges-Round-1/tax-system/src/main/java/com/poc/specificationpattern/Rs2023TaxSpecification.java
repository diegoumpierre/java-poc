package com.poc.specificationpattern;

public class Rs2023TaxSpecification implements TaxSpecification{

    private String state;
    private int year;

    public Rs2023TaxSpecification(String state, int year){
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
