package com.example.problems.dpk01_Revert_String;

public class DPK01_impl_8 {
    public static String revert(String o) {

        if(null == o){
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = o.length()-1; i >= 0 ; i--) {
            stringBuilder.append(o.toCharArray()[i]);
        }

        return stringBuilder.toString();
    }
}
