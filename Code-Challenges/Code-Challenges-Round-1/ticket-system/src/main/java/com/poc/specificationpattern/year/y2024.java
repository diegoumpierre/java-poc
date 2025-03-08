package com.poc.specificationpattern.year;

import com.poc.specificationpattern.TaxSpecification;

public class y2024 implements TaxSpecification {

    private int year;
    private double tax;

    public y2024(int year){
        this.year = year;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {
        boolean satisfied = false;

        if (this.year == year){
            switch (state){
                case "RS":
                    tax = 1.0;
                    satisfied=true;
                    break;
                case "SC":
                    tax = 3.0;
                    satisfied=true;
                    break;
            }
        }

        return satisfied;

    }

    @Override
    public double getTax() {
        return 1.2;
    }
}
