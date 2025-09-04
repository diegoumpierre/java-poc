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

    public static void main(String[] args) throws IOException {
        System.out.println("Running Flight Recorder examples...");
        simpleRecording();
        customEventExample();
        profileMethod();
        System.out.println("Done.");
    }
}

