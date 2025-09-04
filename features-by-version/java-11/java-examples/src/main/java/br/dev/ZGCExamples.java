package br.dev;

/**
 * ZGC (Z Garbage Collector) Example for Java 11
 *
 * ZGC is an experimental low-latency garbage collector introduced in Java 11.
 * It is enabled via JVM options, not in code. This example demonstrates a memory-intensive workload.
 *
 * To run with ZGC, use:
 *   java -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xmx2G br.dev.ZGCExamples
 *
 * Note: ZGC is only available on 64-bit platforms and requires Java 11+.
 */
public class ZGCExamples {
    public static void main(String[] args) {
        System.out.println("Starting ZGC memory allocation example...");
        // Allocate large arrays to trigger GC activity
        for (int i = 0; i < 10; i++) {
            byte[] memoryBlock = new byte[100 * 1024 * 1024]; // 100MB
            System.out.println("Allocated block " + (i + 1) + ": " + memoryBlock.length + " bytes");
            try {
                Thread.sleep(500); // Simulate workload
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Finished memory allocation. Check GC logs for ZGC activity.");
    }
}

