package br.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AexampleApiClassTest {

    @Test
    void something() {
        assertDoesNotThrow(() -> {
            AexampleApiClass aexampleApiClass = new AexampleApiClass();
            aexampleApiClass.something();
        });

    }
}