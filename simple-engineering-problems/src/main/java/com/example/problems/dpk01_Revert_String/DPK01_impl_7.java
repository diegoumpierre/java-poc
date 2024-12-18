package com.example.problems.dpk01_Revert_String;

public class DPK01_impl_7 {

    public static String revert(String input){
        if (null == input) return null;
        StringBuilder res = new StringBuilder();
        for(int i=input.length()-1;i >=0;i--){
            res.append(input.toCharArray()[i]);
        }
        return res.toString();
    }

}
