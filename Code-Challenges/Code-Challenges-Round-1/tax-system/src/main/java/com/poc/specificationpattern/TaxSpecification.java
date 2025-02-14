package com.poc.specificationpattern;

public interface TaxSpecification {
    boolean isSatisfiedBy(String state, int year);
}
