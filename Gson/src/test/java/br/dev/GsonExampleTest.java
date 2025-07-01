package br.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GsonExampleTest {

    @Test
    void something() {
        assertDoesNotThrow(() -> {
            GsonExample aexampleApiClass = new GsonExample();
            aexampleApiClass.something();
        });

    }
}