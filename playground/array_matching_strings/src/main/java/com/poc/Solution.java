package com.poc;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 17 2023
 */
public class Solution {

    /*
     * Complete the 'matchingStrings' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. STRING_ARRAY stringList
     *  2. STRING_ARRAY queries
     */

    public List<Integer> matchingStrings(List<String> stringList, List<String> queries) {

        Map<String,Integer> stringListValues = new HashMap<String, Integer>();

        for(String item :stringList){
            if (stringListValues.containsKey(item)){
                stringListValues.put(item,stringListValues.get(item) +1);
            }else{
                stringListValues.put(item, 1);
            }
        }

        List<Integer> result = new ArrayList<>();

        for(String query :queries){
            if (stringListValues.containsKey(query)){
                result.add(stringListValues.get(query));
            }else{
                result.add(0);
            }
        }

        return result;

    }

}