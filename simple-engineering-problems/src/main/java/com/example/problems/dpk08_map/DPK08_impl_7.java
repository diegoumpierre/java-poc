package com.example.problems.dpk08_map;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DPK08_impl_7 {


    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> result = new ArrayList<>();
        for (T element : list) {
            result.add(function.apply(element));
        }
        return result;
    }


}
