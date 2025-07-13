package br.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalInterfaceExampleTest {


    @Test
    void testGreet() {
       FunctionalInterfaceExample greeting = name -> "Hello, " + name + "!";

        // Test the functional interface implementation
        assertNotNull(greeting);
        assertTrue(greeting instanceof FunctionalInterfaceExample);

        // Test the greet method
        assertEquals("Hello, Alice!", greeting.greet("Alice"));;
        assertEquals("Hello, World!", greeting.greet("World"));

        assertDoesNotThrow(() -> greeting.greet("World"));

    }

}