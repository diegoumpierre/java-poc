package com.poc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Solution {

    //open --> {([   ])}

    //closed

    private Map<String,String> permitedSinal = new HashMap<>();

    private void init(){
        permitedSinal.put("{","}");
        permitedSinal.put("(",")");
        permitedSinal.put("[","]");
    }


    public boolean isBalanced(String input){
        init();
        String[] signalList = new String[input.length()];
        //"{}()", "[{()}]", "({()})"
        for (int i =0; i<input.length();i++){

            char[] lst = input.toCharArray();

            String letter = String.valueOf(lst[i]);

            //verify is open signal
            if (signalList.length == 0 && permitedSinal.containsValue(letter)) return false;

            if (permitedSinal.containsKey(letter)){
                signalList[i] = letter;
            }else {
                if (permitedSinal.containsValue(letter)){
                    //get the last item in the signalList
                    String lastOpen = signalList[signalList.length-1];
                    if (!permitedSinal.get(lastOpen).equals(letter)){
                        return false;
                    }else{
                        ArrayList.po
                        signalList.
                        signalList.pop();

                    }
                }
            }
        }
        return signalList.length == 0;
    }

}
