package com.example.problems.dpk01_Revert_String;

public class DPK01_impl_3 {

    public String revert(String input){
        if (input == null){
            return null;
        }
        String result = "";
        char[] chars = input.toCharArray();
        for(int i=input.length()-1; i >= 0; i--){
            result += chars[i];
        }
        return result;
    }
}
