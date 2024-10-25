package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.StateEnum;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class FeeService {



    public Fee insert(String stateStr, Integer year, Double value){

        StateEnum stateEnum = StateEnum.toEnum(stateStr);

        Random random = new Random();

        Fee fee = Fee.builder()
                .id( Integer.valueOf(String.format("%04d", random.nextInt(10000))) )
                .stateEnum(stateEnum)
                .year(year)
                .value(value).build();


        return fee;
    }

    public void remove(String stateStr, Integer year){
        StateEnum stateEnum = StateEnum.toEnum(stateStr);
        List<Fee> removeItems = new ArrayList<>();

    }

    public List<Fee> getAll(){

        List<Fee> feeList = new ArrayList<>();
        Collections.sort(feeList);
        return feeList;
    }

    public Fee getFeeByStateAndYear(StateEnum stateEnum, Integer year){

        return null;
    }

    //-----------------


    public void insertTaxForTheYear(Integer year, Double value){
        Arrays.stream(StateEnum.values()).forEach(stateEnum -> {
            int i =1;
            insert(stateEnum.name(),year,value);
            i++;
        });
    }

}
