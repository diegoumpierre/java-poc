package com.poc.domain;

import java.util.HashMap;
import java.util.List;

public class Product {

    private String name;
    private List<Tax> taxList;



    public Double getTax(String state, Integer year){

        if (taxList != null && !taxList.isEmpty()){
            for(Tax taxItem : taxList){
                if (taxItem.getState().equals(state) && taxItem.getYear().equals(year)){
                    return taxItem.getValue();
                }
            }
        }
        return null;
    }



}
