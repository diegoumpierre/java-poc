package com.example.problems.dpk05_pointers;

import java.util.HashMap;
import java.util.Map;

public class DPK05_impl_1 {

    private Map<String, Integer> power = Map.of(
            "John",100,
            "Paul", 90,
            "George", 80,
            "Ringo", 70
    );


    public Integer getPower(String name) {


        return power.getOrDefault(name, null);
    }
}
