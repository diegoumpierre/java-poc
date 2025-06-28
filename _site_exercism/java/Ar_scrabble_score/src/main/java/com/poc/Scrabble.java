package com.poc;

import java.util.HashMap;
import java.util.Map;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 16 2023
 */
public class Scrabble {

    private Map<String,Integer> letterValues = new HashMap<>();

    private int score=0;

    private void load(){
        letterValues.put("AEIOULNRST",1);
        letterValues.put("DG",2);
        letterValues.put("BCMP",3);
        letterValues.put("FHVWY",4);
        letterValues.put("K",5);
        letterValues.put("JX",8);
        letterValues.put("QZ",10);

    }

    Scrabble(String word) {
        load();
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            for(String key :letterValues.keySet()){
                if (key.contains(String.valueOf(chars[i]).toUpperCase())){
                    score += letterValues.get(key);
                    break;
                }
            }
        }
    }

    int getScore() {
        return score;
    }
}
