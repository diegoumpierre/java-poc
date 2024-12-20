package com.example.problems.dpk09_filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DPK09_impl_6 {

    public static List<Integer> filter(List<Integer> list, Predicate<Integer> predicate) {
        List<Integer> result = new ArrayList<>();
        for(Integer item :list){
            if (predicate.test(item)){
                result.add(item);
            }
        }
        return result;

    }
}
