package br.dev;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemorySession;
import java.lang.foreign.ValueLayout;

/**
 * Example of Foreign Function & Memory API (Incubator) in Java 17.
 * Demonstrates allocating native memory, writing, and reading values.
 * Note: This API is incubating and may require JVM flags to enable.
 */
public class ForeignFunctionMemoryApiExample {
    public static void main(String[] args) {
        // Open a memory session (try-with-resources ensures cleanup)
        try (MemorySession session = MemorySession.openConfined()) {
            // Allocate native memory for 3 integers
            MemorySegment segment = session.allocate(3 * ValueLayout.JAVA_INT.byteSize());

            // Write values to native memory
            segment.setAtIndex(ValueLayout.JAVA_INT, 0, 42);
            segment.setAtIndex(ValueLayout.JAVA_INT, 1, 99);
            segment.setAtIndex(ValueLayout.JAVA_INT, 2, -7);

            // Read values from native memory
            int first = segment.getAtIndex(ValueLayout.JAVA_INT, 0);
            int second = segment.getAtIndex(ValueLayout.JAVA_INT, 1);
            int third = segment.getAtIndex(ValueLayout.JAVA_INT, 2);

            System.out.println("Values from native memory:");
            System.out.println("First:  " + first);
            System.out.println("Second: " + second);
            System.out.println("Third:  " + third);
        }
    }
}

