package com.poc;

import java.util.HashMap;
import java.util.Map;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 21 2023
 */
public class Solution {
    public boolean verifyDuplicate(int input[]){

        Map<Integer, Integer> visited = new HashMap<>();

        for(int i=0;i<input.length;i++){
            if(visited.containsKey(input[i])){
                return true;
            }
            visited.put(input[i],1);

        }
        return false;
    }
}
