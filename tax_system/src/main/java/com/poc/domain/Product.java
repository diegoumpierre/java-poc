package com.poc.domain;

import java.util.List;

public class Product {

    private String name;
    private List<Tax> taxList;



    public Double getTax(Estate state, Integer year){

        if (taxList != null && !taxList.isEmpty()){
            for(Tax taxItem : taxList){
                if (taxItem.getState().equals(state) && taxItem.getYear().equals(year)){
                    return taxItem.getValue();
                }
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tax> getTaxList() {
        return taxList;
    }

    public void setTaxList(List<Tax> taxList) {
        this.taxList = taxList;
    }
}
