package com.umpierre.functionalinterfaces;

@FunctionalInterface
public interface ParamsProcessor<Integer> {
    Integer process(Integer param1, Integer param2);
}
