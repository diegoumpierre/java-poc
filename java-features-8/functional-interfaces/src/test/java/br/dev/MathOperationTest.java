package br.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathOperationTest {

    @Test
    void testOperation() {
        MathOperation addition = (a, b) -> a + b;
        MathOperation subtraction = (a, b) -> a - b;
        MathOperation multiplication = (a, b) -> a * b;
        MathOperation division = (a, b) -> a / b;

        assertEquals(5, addition.operate(2, 3));
        assertEquals(-1, subtraction.operate(2, 3));
        assertEquals(6, multiplication.operate(2, 3));
        assertEquals(2, division.operate(6, 3));
    }
}