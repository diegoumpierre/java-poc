package com.example.problems.dpk11_replace;

public class DPK11_impl_2 {
    public static String replace(String input, String token, String newToken) {
        String result = "";
        boolean toReplace = false;

        for (int i = 0; i < input.length(); i++) {
            for (int j = 0; j < token.length(); j++) {
                toReplace = true;
                if((i+j) < input.length() && input.charAt(i+j) != token.charAt(j)){
                    toReplace= false;
                    break;
                }
            }
            if (toReplace){
                result += newToken;
                i +=token.length()-1;
            }else{
                result += input.charAt(i);
            }
        }
        return result;
    }
}
