package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.StateEnum;
import com.poc.domain.TaxSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class FeeService {

    @Autowired
    ProductService productService;

    public Fee insert(String stateStr, Integer year, Double value){

        StateEnum stateEnum = StateEnum.toEnum(stateStr);

        Fee fee = Fee.builder()
                .stateEnum(stateEnum)
                .year(year)
                .value(value).build();

        TaxSystem.FEE_LIST.add(fee);
        return fee;
    }

    public void remove(String stateStr, Integer year){
        StateEnum stateEnum = StateEnum.toEnum(stateStr);
        List<Fee> removeItems = new ArrayList<>();
        TaxSystem.FEE_LIST.forEach(fee->{
            if (fee.getStateEnum().equals(stateEnum) && fee.getYear().equals(year)){
                removeItems.add(fee);
                productService.removeFeeFromProduct(fee);
            }
        });
        TaxSystem.FEE_LIST.removeAll(removeItems);
    }

    public List<Fee> getAll(){
        return new ArrayList<>(TaxSystem.FEE_LIST);
    }

    public Fee getFeeByStateAndYear(StateEnum stateEnum, Integer year){

        for(Fee fee :TaxSystem.FEE_LIST){
            if (fee.getStateEnum().equals(stateEnum) && fee.getYear().equals(year)){
                return fee;
            }
        }
        return null;
    }

    //-----------------

//    public void removeAllTaxesBefore(Integer year){
//
//        //to remove a Fee we need sure remove from the product
//        for(Fee fee :TaxSystem.FEE_LIST){
//            if (fee.getYear() < year){
//                //remove from the product who has this fee
//                productService.removeFeeFromProduct(fee);
//                TaxSystem.FEE_LIST.remove(fee);
//            }
//        }
//    }

    public void insertTaxForTheYear(Integer year, Double value){
        Arrays.stream(StateEnum.values()).forEach(stateEnum -> insert(stateEnum.name(),year,value));
    }

}
