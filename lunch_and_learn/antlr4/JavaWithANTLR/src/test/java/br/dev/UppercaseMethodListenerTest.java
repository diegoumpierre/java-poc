package br.dev;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UppercaseMethodListenerTest {

    @Test
    void something() {
        UppercaseMethodListener listener = new UppercaseMethodListener();
        String input = "public class TestClass { public void MyMethod() {} }";

        // Simulate parsing the input and invoking the listener
        // This part would typically involve using ANTLR to parse the input
        // and call the listener methods accordingly.


        listener.enterMethodDeclarator(input);


        // For demonstration, we will assume the listener has been invoked
        // and we can check for errors.

        assertTrue(listener.getErrors().isEmpty(), "There should be no errors for valid method names.");

        // If we had a method that was uppercased, we would check for that error
        // For example, if we had "public void MyMethod() {}" in the input,
        // we would expect an error.
    }
}