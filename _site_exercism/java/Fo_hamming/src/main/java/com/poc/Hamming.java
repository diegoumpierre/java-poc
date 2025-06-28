package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
public class Hamming {

    private int hammingDistance = 0;

    public Hamming(String leftStrand, String rightStrand) {
        hammingDistance = 0;
        if (leftStrand == null || rightStrand == null
               || (leftStrand.length() != rightStrand.length())){
            throw new IllegalArgumentException("strands must be of equal length");
        }
        for(int i=0; i<leftStrand.length();i++){
            if (leftStrand.charAt(i) != rightStrand.charAt(i)){
                hammingDistance ++;
            }
        }
    }

    public int getHammingDistance() {
        return hammingDistance;
    }
}
