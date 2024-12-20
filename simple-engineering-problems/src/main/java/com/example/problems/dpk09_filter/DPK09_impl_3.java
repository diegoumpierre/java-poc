package com.example.problems.dpk09_filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DPK09_impl_3 {
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
