package com.poc.criptografia;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CriptoTest {

    @Test
    void encrypt() {
        String secret = "keypass";
        String toEncode = "Diego is testing";
        String result = Cripto.encrypt(toEncode, secret);
        String expectedResult = "PD2fzVyRJ4Sojnf8tEVXyRfUl2D1BI5fLCgJlSRJTkI=";
        assertEquals(expectedResult, result);
    }

    @Test
    void decrypt() {
        String secret = "keypass";
        String fromEncode = "PD2fzVyRJ4Sojnf8tEVXyRfUl2D1BI5fLCgJlSRJTkI=";
        String result = Cripto.decrypt(fromEncode, secret);
        String expectedResult = "Diego is testing";
        assertEquals(expectedResult, result);
    }

    @Test
    void setKey() {
    }

    @Test
    void testEncrypt() {
    }

    @Test
    void testDecrypt() {
    }

    @Test
    void loadKeySecret() {
    }
}