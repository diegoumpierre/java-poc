package com.poc;

import java.io.UnsupportedEncodingException;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Oct 19 2023
 */
public class Solution {
    private int size = 5;

    public String truncate(String input) {
        if (input.codePoints().count() <= size) return input;
        int byteSize  = input.offsetByCodePoints(0,size);
        return input.substring(0,byteSize);
    }
}