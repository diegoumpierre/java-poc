package com.poc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Solution {
    public List<Integer> compareTriplets(List<Integer> a, List<Integer> b) {
        int sumA = 0;
        int sumB = 0;

        for (int i = 0; i < a.size(); i++) {
            if (a.get(i) == b.get(i)) continue;

            if (a.get(i) > b.get(i)) sumA++;
            if (a.get(i) < b.get(i)) sumB++;

        }
        return new ArrayList<>(Arrays.asList(sumA, sumB));
    }
}
