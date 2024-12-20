package com.example.problems.dpk09_filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DPK09_impl_8 {

    public static List<Integer> filter(List<Integer> input, Predicate<Integer> predicate) {
        List<Integer> result = new ArrayList<>();
        for(Integer item :input){
            if (predicate.test(item)){
                result.add(item);
            }
        }
        return result;
    }
}
