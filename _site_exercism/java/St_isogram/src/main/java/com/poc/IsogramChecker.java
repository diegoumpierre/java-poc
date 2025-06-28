package com.poc;

import java.util.HashSet;
import java.util.Set;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Jan 03 2024
 */
public class IsogramChecker {
    boolean isIsogram(String phrase) {
        Set<String> chars = new HashSet<>();

        for(char c :phrase.toCharArray()){
            String current = String.valueOf(c).toUpperCase();
            if (current.equals(" ") || current.equals("-")) continue;
            if (!chars.add(current)) return false;
        }
        return true;
    }
}
