package com.example.problems.dpk07_group_by;

import java.util.ArrayList;
import java.util.List;

public class DPK07_impl_10 {

    public static List<List<Object>> group_by(List<Object> inputList, int size) {
        List<List<Object>> result = new ArrayList<>();

        List<Object> spot = new ArrayList<>();
        int sizeCount = 0;
        for (Object item :inputList){
            if (sizeCount < size){
                spot.add(item);
                sizeCount++;
            }else{
                result.add(spot);
                spot = new ArrayList<>();
                spot.add(item);
                sizeCount = 1;
            }
        }
        result.add(spot);
        return result;
    }


}
