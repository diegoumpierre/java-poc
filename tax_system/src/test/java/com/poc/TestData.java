package com.poc;

import com.poc.domain.Product;
import com.poc.domain.Tax;

import java.util.ArrayList;
import java.util.List;

public class TestData {




    public static Tax gimmeTax(Tax.StateEnum stateEnum, Integer year, Double value){
        Tax tax = new Tax();
        tax.setState(stateEnum);
        tax.setYear(year);
        tax.setValue(value);
        return tax;
    }
    public static List<Tax> gimmeListTax() {

        List<Tax> taxList = new ArrayList<>();
        taxList.add(gimmeTax(Tax.StateEnum.RS,2015, 20.3));
        taxList.add(gimmeTax(Tax.StateEnum.RS,2010, 30.3));
        taxList.add(gimmeTax(Tax.StateEnum.RS,2009, 50.3));
        taxList.add(gimmeTax(Tax.StateEnum.RJ,2010, 60.3));
        taxList.add(gimmeTax(Tax.StateEnum.RJ,2011, 20.7));
        taxList.add(gimmeTax(Tax.StateEnum.RJ,2009, 20.8));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2010, 24.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2011, 22.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2010, 24.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2011, 22.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2010, 24.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2011, 22.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2010, 24.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2011, 22.3));
        taxList.add(gimmeTax(Tax.StateEnum.SC,2010, 24.3));

        return taxList;
    }


}
