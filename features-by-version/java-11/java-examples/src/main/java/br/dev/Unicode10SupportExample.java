package br.dev;

/**
 * Unicode 10 Support Example (Java 11)
 *
 * Java 11 supports Unicode 10, including new emoji and scripts.
 * This example demonstrates using and printing Unicode 10 characters.
 */
public class Unicode10SupportExample {
    public static void main(String[] args) {
        // Unicode 10 emoji and symbols
        String unicodeEmoji = "\uD83E\uDDC0"; // 🧀 (CHEESE WEDGE, Unicode 10)
        String unicodeScript = "\u20BF"; // ₿ (BITCOIN SIGN, Unicode 10)
        String unicodeOther = "\uD83E\uDDC2"; // 🧂 (SALT SHAKER, Unicode 10)

        System.out.println("Unicode 10 Emoji: " + unicodeEmoji);
        System.out.println("Unicode 10 Script: " + unicodeScript);
        System.out.println("Unicode 10 Other: " + unicodeOther);

        // Iterate over code points
        String all = unicodeEmoji + " " + unicodeScript + " " + unicodeOther;
        System.out.println("Iterating code points:");
        all.codePoints().forEach(cp -> System.out.printf("U+%X (%s)\n", cp, new String(Character.toChars(cp))));
    }
}

