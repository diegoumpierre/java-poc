package com.poc.specification;

import com.poc.domain.Tax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private Map<String, List<Tax>> listMap = new HashMap<>();

    public void addTax(String year, Tax tax){

        listMap.getOrDefault(year,new ArrayList<>());

    }

    public void removeTax(){

    }
}
