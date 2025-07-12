package br.dev;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


public class FunctionalInterfaceTest {


    @Test
    void functionTransformInputToOutput(){
        Function<String, Integer> stringToLength = String::length;
        assertEquals(5, stringToLength.apply("Hello"));
    }

    @Test
    void predicate(){
        Predicate<String> isNotEmpty = str -> !str.isEmpty();
        assertTrue(isNotEmpty.test("Hello"));
        assertFalse(isNotEmpty.test(""));
    }

    @Test
    void consumer(){
        StringBuilder sb = new StringBuilder();
        Consumer<String> appendConsumer = sb::append;
        appendConsumer.accept("Hello, ");
        appendConsumer.accept("World!");
        assertEquals("Hello, World!", sb.toString());
    }

    @Test
    void supplier(){
        // Supplier does not take any input, just returns a value
        Supplier<String> stringSupplier = () -> "Hello, Supplier!";
        assertEquals("Hello, Supplier!", stringSupplier.get());
    }

}
