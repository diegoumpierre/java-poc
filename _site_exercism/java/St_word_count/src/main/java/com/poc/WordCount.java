package com.poc;

import java.util.HashMap;
import java.util.Map;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since JAN 04 2024
 */
public class WordCount {

    private Map<String, Integer> wordCount;

    public Map<String, Integer> phrase(String input) {
        wordCount = new HashMap<>();

        String[] wordsSpace = cleanWordIgnoreCaracters(input.toLowerCase()).split(" ");
        for (String wSpace : wordsSpace) {
            String[] wordsComma = wSpace.split(",");
            doCount(wordsComma);
        }
        return wordCount;
    }

    private String cleanWordIgnoreCaracters(String input){
        input = input.replaceAll("\n","");
        input = input.replaceAll("!","");
        input = input.replaceAll("&","");
        input = input.replaceAll("@","");
        input = input.replaceAll("\\$","");
        input = input.replaceAll("%","");
        input = input.replaceAll("\\^","");
        input = input.replaceAll(":","");
        input = input.replaceAll("\\.","");
        input = input.replaceAll("\\.","");

        return input;
    }
    private void doCount(String[] words){
        for(String word :words){

            if (word.startsWith("'") && word.endsWith("'")){
                word = word.replaceFirst("'","");
                word = word.substring(0,word.length()-1);
            }
            if (word.trim().equals("")) continue;

            if (wordCount.containsKey(word)){
                wordCount.put(word, wordCount.get(word)+1);
            }else{
                wordCount.put(word, 1);
            }
        }
    }
}
