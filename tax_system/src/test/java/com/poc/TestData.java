package com.poc;

import com.poc.domain.Product;
import com.poc.domain.Tax;

import java.util.ArrayList;
import java.util.List;

public class TestData {




    public List<Tax> gimmeListTax() {

        List<Tax> taxList = new ArrayList<>();

        Tax tax = new Tax();
        tax.setState(Tax.StateEnum.RS);
        tax.setYear(2010);
        tax.setValue(20.3);
        taxList.add(tax);

        Tax tax1 = new Tax();
        tax1.setState(Tax.StateEnum.RS);
        tax1.setYear(2010);
        tax1.setValue(20.3);
        taxList.add(tax1);

        Tax tax2 = new Tax();
        tax2.setState(Tax.StateEnum.RS);
        tax2.setYear(2010);
        tax2.setValue(20.3);
        taxList.add(tax2);

        Tax tax3 = new Tax();
        tax3.setState(Tax.StateEnum.RS);
        tax3.setYear(2010);
        tax3.setValue(20.3);
        taxList.add(tax3);

        return taxList;
    }


}
