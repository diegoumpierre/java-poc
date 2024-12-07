package com.example.problems.dpk01_Revert_String;

public class DPK01_impl_10 {

    public static String revert(String input){
        if (null == input){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = input.length()-1; i >= 0 ; i--) {
            stringBuilder.append(input.toCharArray()[i]);
        }
        return stringBuilder.toString();
    }
}
