package com.poc;

import java.util.ArrayList;
import java.util.List;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 20 2023
 */
public class Solution {
    public List<Integer> reverseArray(List<Integer> a) {
        // Write your code here
        List<Integer> result = new ArrayList<>();
        for(int i = a.size() -1; i >=0 ; i--){
            result.add(a.get(i));
        }
        return result;

    }
}
