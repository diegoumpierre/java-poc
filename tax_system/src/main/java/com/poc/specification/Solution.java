package com.poc.specification;

import com.poc.domain.Tax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

    private Map<Long, Tax> listMap = new HashMap<>();





    public Tax searchByYear(Long year){
        return listMap.getOrDefault(year,new Tax());
    }

    public void removeTax(Long year, Tax.MonthEnum month ){

        Tax tax = listMap.get(year);
        tax.getApr().equals(month);
        listMap.remove(year);
    }
}
