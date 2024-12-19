package com.example.problems.dpk06_tokenizer;

import java.util.ArrayList;
import java.util.List;

public class DPK06_impl_4 {

    public static List<String> tokenize(String phase, String token) {
        List<String> result = new ArrayList<>();
        StringBuffer word = new StringBuffer();

        for(char letter :phase.toCharArray()){
            if (token.charAt(0) == letter){
                result.add(word.toString());
                word = new StringBuffer();
            }else{
                word.append(letter);
            }
        }
        result.add(word.toString());
        return result;
    }


}
