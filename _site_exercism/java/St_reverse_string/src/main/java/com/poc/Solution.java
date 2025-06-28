package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Solution {
    public String basicMethod(String wordToReverse){
        char[] word = wordToReverse.toCharArray();
        char[] reverse = new char[word.length];

        String reverseWord = "";


        int reverseIndice = 0;
        for(int i = word.length-1; i >= 0; i--){
            reverse[reverseIndice] = word[i];
            reverseIndice++;
            reverseWord += word[i];
        }

        return reverseWord;
       // return String.valueOf(reverse);
    }
}
