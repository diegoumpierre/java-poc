package br.dev;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.Arena;
import java.lang.foreign.ValueLayout;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

public class ForeignFunctionMemoryApiV2Example {
    public static void main(String[] args) throws Throwable {
        // Allocate native memory for a string
        try (Arena arena = Arena.ofConfined()) {
            String message = "Hello from Java 25!";
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            MemorySegment segment = arena.allocate(bytes.length + 1, 1); // +1 for null terminator
            segment.asByteBuffer().put(bytes);
            segment.set(ValueLayout.JAVA_BYTE, bytes.length, (byte) 0); // null terminator

            // Example: call C's strlen function
            Linker linker = Linker.nativeLinker();
            SymbolLookup stdlib = linker.defaultLookup();
            MethodHandle strlen = linker.downcallHandle(
                stdlib.find("strlen").get(),
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
            );
            long len = (long) strlen.invoke(segment);
            System.out.println("Length of string in native memory: " + len);
        }
    }
}

