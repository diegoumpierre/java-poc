package com.example.problems.dpk11_replace;

import java.util.List;
import java.util.function.BinaryOperator;

public class DPK11_impl_1 {

    public static String replace(String input, String token, String replaceBy) {
        String result = new String();

        result = input.replaceAll(token,replaceBy);


        return result;
    }
}
