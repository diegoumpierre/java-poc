package com.poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Sep 12 2023
 */
class SolutionTest {
    private Solution solution;
    @BeforeEach
    void init(){
        solution = new Solution();
    }

    @Test
    public void englishLanguageShort() {
        String expected = "Hi";
        assertEquals(expected, solution.truncate("Hi"));
    }

    @Test
    public void englishLanguageLong() {
        String expected = "Hello";
        assertEquals(expected, solution.truncate("Hello there"));
    }


    @Test
    public void germanLanguageShort_broth() {
        String expected = "brühe";
        assertEquals(expected, solution.truncate("brühe"));
    }

    @Test
    public void germanLanguageLong_bearCarpet_to_beards() {
        String expected = "Bärte";
        assertEquals(expected, solution.truncate("Bärteppich"));
    }


    @Test
    public void bulgarianLanguageShort_good() {
        String expected = "Добър";
        assertEquals(expected, solution.truncate("Добър"));
    }

    @Test
    public void greekLanguageShort_health() {
        String expected = "υγειά";
        assertEquals(expected, solution.truncate("υγειά"));
    }


    @Test
    public void mathsShort() {
        String expected = "a=πr²";
        assertEquals(expected, solution.truncate("a=πr²"));
    }

    @Test
    public void mathsLong() {
        String expected = "∅⊊ℕ⊊ℤ";
        assertEquals(expected, solution.truncate("∅⊊ℕ⊊ℤ⊊ℚ⊊ℝ⊊ℂ"));
    }
    
    @Test
    public void englishAndEmojiShort() {
        String expected = "Fly 🛫";
        assertEquals(expected, solution.truncate("Fly 🛫"));
    }
    
    @Test
    public void emojiShort() {
        String expected = "💇";
        assertEquals(expected, solution.truncate("💇"));
    }
    
    @Test
    public void emojiLong() {
        String expected = "❄🌡🤧🤒🏥";
        assertEquals(expected, solution.truncate("❄🌡🤧🤒🏥🕰😀"));
    }
    
    @Test
    public void royalFlush() {
        String expected = "🃎🂸🃅🃋🃍";
        assertEquals(expected, solution.truncate("🃎🂸🃅🃋🃍🃁🃊"));
    }

}