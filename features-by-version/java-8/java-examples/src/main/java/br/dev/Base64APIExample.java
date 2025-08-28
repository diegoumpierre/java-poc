package main.java.br.dev;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Examples of using Java 8's Base64 API.
 */
public class Base64APIExample {
    public static void main(String[] args) {
        // 1. Basic Encoding and Decoding
        String original = "Java 8 Base64 Example!";
        String encoded = Base64.getEncoder().encodeToString(original.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encoded: " + encoded);
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        System.out.println("Decoded: " + decoded);

        // 2. Encoding and Decoding a Byte Array
        byte[] data = {1, 2, 3, 4, 5};
        String encodedBytes = Base64.getEncoder().encodeToString(data);
        System.out.println("Encoded bytes: " + encodedBytes);
        byte[] decodedBytes = Base64.getDecoder().decode(encodedBytes);
        System.out.print("Decoded bytes: ");
        for (byte b : decodedBytes) System.out.print(b + " ");
        System.out.println();

        // 3. URL and Filename Safe Encoding
        String url = "https://github.com/Java8?query=base64+api";
        String urlEncoded = Base64.getUrlEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
        System.out.println("URL Encoded: " + urlEncoded);
        String urlDecoded = new String(Base64.getUrlDecoder().decode(urlEncoded), StandardCharsets.UTF_8);
        System.out.println("URL Decoded: " + urlDecoded);

        // 4. MIME Encoding (for emails, etc.)
        String longText = "This is a long text that will be split into multiple lines by MIME encoder.";
        String mimeEncoded = Base64.getMimeEncoder().encodeToString(longText.getBytes(StandardCharsets.UTF_8));
        System.out.println("MIME Encoded: " + mimeEncoded);
        String mimeDecoded = new String(Base64.getMimeDecoder().decode(mimeEncoded), StandardCharsets.UTF_8);
        System.out.println("MIME Decoded: " + mimeDecoded);

        // 5. Handling Invalid Input
        try {
            Base64.getDecoder().decode("invalid base64");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception for invalid input: " + e.getMessage());
        }
    }
}

/**
 * Output:
 Encoded: SmF2YSA4IEJhc2U2NCBFeGFtcGxlIQ==
 Decoded: Java 8 Base64 Example!
 Encoded bytes: AQIDBAU=
 Decoded bytes: 1 2 3 4 5
 URL Encoded: aHR0cHM6Ly9naXRodWIuY29tL0phdmE4P3F1ZXJ5PWJhc2U2NCthcGk=
 URL Decoded: https://github.com/Java8?query=base64+api
 MIME Encoded: VGhpcyBpcyBhIGxvbmcgdGV4dCB0aGF0IHdpbGwgYmUgc3BsaXQgaW50byBtdWx0aXBsZSBsaW5l
 cyBieSBNSU1FIGVuY29kZXIu
 MIME Decoded: This is a long text that will be split into multiple lines by MIME encoder.
 Caught exception for invalid input: Illegal base64 character 20

 */
