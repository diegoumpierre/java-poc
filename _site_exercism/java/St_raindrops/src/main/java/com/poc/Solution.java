package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Solution {
    String convert(int number) {
        String result = "";

        if (number%3 == 0) result +="Pling";
        if (number%5 == 0) result +="Plang";
        if (number%7 == 0) result +="Plong";

        if (result == "") return String.valueOf(number);

        return result;
    }
}
