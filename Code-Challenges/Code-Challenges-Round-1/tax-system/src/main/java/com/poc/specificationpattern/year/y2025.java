package com.poc.specificationpattern.year;

import com.poc.specificationpattern.TaxSpecification;

public class y2025  implements TaxSpecification {

    private int year;
    private double tax;
    public y2025(int year){
        this.year = year;
    }

    @Override
    public boolean isSatisfiedBy(String state, int year) {
        boolean isSatisfied = false;
        if (this.year == year){
            switch (state){
                case "RS":
                    this.tax = 1.0;
                    isSatisfied=true;
                    break;
                case "SC":
                    this.tax = 1.2;
                    isSatisfied=true;
                    break;
            }
        }
        return isSatisfied;

    }

    @Override
    public double getTax() {
        return 1.2;
    }
}
