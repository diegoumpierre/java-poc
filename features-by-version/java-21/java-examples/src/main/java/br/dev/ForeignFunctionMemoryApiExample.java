package br.dev;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.Arena;

public class ForeignFunctionMemoryApiExample {
    public static void main(String[] args) {
        // Allocate native memory for one int
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = arena.allocate(ValueLayout.JAVA_INT);
            segment.set(ValueLayout.JAVA_INT, 0, 42); // Write value
            int value = segment.get(ValueLayout.JAVA_INT, 0); // Read value
            System.out.println("Value in native memory: " + value);
        }
        // Example: linking to a native C function (requires setup)
        // See Java 21 docs for Linker and SymbolLookup usage
    }
}

