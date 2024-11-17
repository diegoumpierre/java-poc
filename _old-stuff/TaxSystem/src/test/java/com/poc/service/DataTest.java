package com.poc.service;

import com.poc.domain.Fee;
import com.poc.domain.StateEnum;

import java.util.ArrayList;
import java.util.List;

public class DataTest {


    public static Fee gimmeFee(){
        return Fee.builder()
                .value(10d)
                .year(2020)
                .stateEnum(StateEnum.RS)
                .build();
    }


    public static List<Fee> gimmeFeeList(int quantity) {

        List<Fee> feeList = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            feeList.add(Fee.builder()
                    .value(10d)
                    .year(2020)
                    .stateEnum(StateEnum.RS)
                    .build());
        }
        return feeList;
    }

}
