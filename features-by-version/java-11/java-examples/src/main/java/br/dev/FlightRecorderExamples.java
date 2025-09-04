package br.dev;

import jdk.jfr.Recording;
import jdk.jfr.Event;
import jdk.jfr.Label;
import java.io.IOException;
import java.nio.file.Path;

public class FlightRecorderExamples {
    // Example 1: Start and stop a simple JFR recording
    public static void simpleRecording() throws IOException {
        try (Recording recording = new Recording()) {
            recording.setName("SimpleRecording");
            recording.start();
            // Simulate workload
            for (int i = 0; i < 1000000; i++) {
                Math.sqrt(i);
            }
            recording.stop();
            recording.dump(Path.of("simple-recording.jfr"));
            System.out.println("Recording saved as simple-recording.jfr");
        }
    }

    // Example 2: Custom JFR Event
    @Label("Custom Event Example")
    static class CustomEvent extends Event {
        @Label("Message")
        String message;
        CustomEvent(String message) {
            this.message = message;
        }
    }

    public static void customEventExample() {
        CustomEvent event = new CustomEvent("Hello from JFR custom event!");
        event.commit();
        System.out.println("Custom JFR event committed.");
    }

    // Example 3: Profile a method with JFR
    public static void profileMethod() throws IOException {
        try (Recording recording = new Recording()) {
            recording.setName("ProfileMethod");
            recording.start();
            // Profiled code
            long sum = 0;
            for (int i = 0; i < 10000000; i++) {
                sum += i;
            }
            recording.stop();
            recording.dump(Path.of("profile-method.jfr"));
            System.out.println("Profiled method, recording saved as profile-method.jfr");
        }
    }

    // Example 4: Record a simulated HTTP request
    public static void recordHttpRequest() throws IOException {
        try (Recording recording = new Recording()) {
            recording.setName("HttpRequestRecording");
            recording.start();
            // Simulate HTTP request
            try {
                Thread.sleep(200); // Simulate network latency
                System.out.println("Simulated HTTP request completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            recording.stop();
            recording.dump(Path.of("http-request-recording.jfr"));
            System.out.println("HTTP request recording saved as http-request-recording.jfr");
        }
    }

    // Example 5: Record a simulated database query
    public static void recordDatabaseQuery() throws IOException {
        try (Recording recording = new Recording()) {
            recording.setName("DatabaseQueryRecording");
            recording.start();
            // Simulate database query
            try {
                Thread.sleep(150); // Simulate query time
                System.out.println("Simulated database query completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            recording.stop();
            recording.dump(Path.of("database-query-recording.jfr"));
            System.out.println("Database query recording saved as database-query-recording.jfr");
        }
    }

    // Example 6: Record a simulated file read
    public static void recordFileRead() throws IOException {
        try (Recording recording = new Recording()) {
            recording.setName("FileReadRecording");
            recording.start();
            // Simulate file read
            try {
                Thread.sleep(100); // Simulate file read time
                System.out.println("Simulated file read completed.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            recording.stop();
            recording.dump(Path.of("file-read-recording.jfr"));
            System.out.println("File read recording saved as file-read-recording.jfr");
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Running Flight Recorder examples...");
        simpleRecording();
        customEventExample();
        profileMethod();
        recordHttpRequest();
        recordDatabaseQuery();
        recordFileRead();
        System.out.println("Done.");
    }
}
