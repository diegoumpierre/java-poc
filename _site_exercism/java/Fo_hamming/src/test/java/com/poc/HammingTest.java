package com.poc;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class HammingTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testNoDistanceBetweenEmptyStrands() {
        Hamming hamming = new Hamming("","");
        assertEquals(0, hamming.getHammingDistance());
    }

    @Test
    public void testNoDistanceBetweenShortIdenticalStrands() {
        Hamming hamming = new Hamming("A","A");
        assertEquals(0, hamming.getHammingDistance());
    }

    @Test
    public void testCompleteDistanceInSingleLetterDifferentStrands() {
        Hamming hamming = new Hamming("G","T");
        assertEquals(1, hamming.getHammingDistance());
    }

    @Test
    public void testDistanceInLongIdenticalStrands() {
        Hamming hamming = new Hamming("GGACTGAAATCTG","GGACTGAAATCTG");
        assertEquals(0, hamming.getHammingDistance());
    }

    @Test
    public void testDistanceInLongDifferentStrands() {
        Hamming hamming = new Hamming("GGACGGATTCTG","AGGACGGATTCT");
        assertEquals(9, hamming.getHammingDistance());

    }

    @Test
    public void testValidatesFirstStrandNotLonger() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Hamming("AATG", "AAA"))
                .withMessage("strands must be of equal length");
    }

    @Test
    public void testValidatesSecondStrandNotLonger() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Hamming("ATA", "AGTG"))
                .withMessage("strands must be of equal length");

    }

    @Test
    public void testDisallowLeftEmptyStrand() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Hamming("", "G"))
                .withMessage("strands must be of equal length");

    }

    @Test
    public void testDisallowRightEmptyStrand() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Hamming("G", ""))
                .withMessage("strands must be of equal length");
    }


}