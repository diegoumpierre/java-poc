package com.poc.domain;

import java.util.List;

public class Product {

    private String name;
    private List<Tax> taxList;



    public Double getTax(Estate state, Integer year){


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
