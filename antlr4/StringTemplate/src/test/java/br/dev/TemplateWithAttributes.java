package br.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateWithAttributes {

    @Test
    void something() {
        assertDoesNotThrow(() -> {
            TemplateWithAttributes templateWithAttributes = new TemplateWithAttributes();
            templateWithAttributes.something();
        });

    }
}