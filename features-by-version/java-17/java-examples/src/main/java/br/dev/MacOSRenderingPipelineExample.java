package br.dev;

/**
 * Example for New macOS Rendering Pipeline in Java 17
 *
 * Java 17 introduces a new rendering pipeline for macOS (Metal),
 * replacing the old OpenGL-based pipeline. This is mostly transparent
 * to developers, but you can verify and experiment with it by running
 * Java GUI applications (like Swing or JavaFX) on macOS.
 *
 * This example launches a simple Swing window. To explicitly enable the Metal pipeline,
 * run with: -Dsun.java2d.metal=true
 *
 * To disable Metal and use OpenGL (if available):
 *   -Dsun.java2d.opengl=true -Dsun.java2d.metal=false
 *
 * Note: This example is only meaningful on macOS.
 */
import javax.swing.*;
import java.awt.*;

public class MacOSRenderingPipelineExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("macOS Rendering Pipeline Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("Java 17 macOS Rendering Pipeline (Metal)", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            frame.add(label);

            frame.setVisible(true);
        });
    }
}

