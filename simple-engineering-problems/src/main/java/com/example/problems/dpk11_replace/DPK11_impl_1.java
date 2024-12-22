package com.example.problems.dpk11_replace;

public class DPK11_impl_1 {

    public static String replace(String input, String token, String newToken) {
        String result = "";

        char[] tokenToReplace = token.toCharArray();
        char[] inputString = input.toCharArray();

        for(int i=0;i<input.length();i++){
            boolean toReplace = false;
            for(int j=0;j<tokenToReplace.length;j++){
                toReplace = true;
                if ((i+j) < inputString.length && inputString[i+j] != tokenToReplace[j]){
                    toReplace = false;
                    break;
                }
            }
            if (toReplace) {
                result += newToken;
                i += token.length()-1;
            }else{
                result += inputString[i];
            }

        }
        return result;
    }

}
