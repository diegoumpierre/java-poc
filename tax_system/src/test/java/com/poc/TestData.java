package com.poc;

import com.poc.domain.Product;
import com.poc.domain.Tax;

import java.util.ArrayList;
import java.util.List;

public class TestData {


 public static List<Tax> gimmeListTax() {

        List<Tax> taxList = new ArrayList<>();
        taxList.add(new Tax(Tax.StateEnum.RS,2010,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,1.0,1.1,1.2,1.3));
        taxList.add(new Tax(Tax.StateEnum.RJ,2010,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,1.0,1.1,1.2,1.3));
        taxList.add(new Tax(Tax.StateEnum.SC,2010,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,1.0,1.1,1.2,1.3));
        taxList.add(new Tax(Tax.StateEnum.SP,2010,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,1.0,1.1,1.2,1.3));
        return taxList;
    }


}
