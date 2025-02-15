package com.poc.specificationpattern;

public interface TaxSpecification {
    boolean isSatisfiedBy(String state, int year);

    double getTax();
    //add the second method to get the tax

    //trade off
    // split by state, year or the both
    // what is the best to use (pro and cons)
    // by year the code is self contained
    //what it the consequence of that

}
