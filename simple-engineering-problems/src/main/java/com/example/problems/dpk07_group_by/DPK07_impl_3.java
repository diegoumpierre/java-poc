package com.example.problems.dpk07_group_by;

import java.util.ArrayList;
import java.util.List;

public class DPK07_impl_3 {

    public static List<List<Object>> group_by(List<Object> input, int size){
        List<List<Object>> result = new ArrayList<>();

        List<Object> spot = new ArrayList<>();
        int count = 0;
        for(Object item :input){
            if(count <size){
                spot.add(item);
                count++;
            }else{
                result.add(spot);
                spot = new ArrayList<>();
                spot.add(item);
                count=1;
            }
        }
        result.add(spot);
        return result;
    }

}
