package br.dev;

/*
A sealed class in Java is a class that restricts which other classes or interfaces can extend or implement it.
You define a sealed class using the sealed modifier and specify the permitted subclasses with the permits clause.
 Only the listed classes can directly extend the sealed class, providing more control over the class hierarchy and
 improving code safety and maintainability.

 */
public class SealedClassExample {
    // Sealed class Shape permits only Circle and Rectangle
    public sealed static class Shape permits Circle, Rectangle {
        public double area() {
            return 0.0;
        }
    }

    // Only permitted subclass: Circle
    public static final class Circle extends Shape {
        private final double radius;
        public Circle(double radius) {
            this.radius = radius;
        }
        @Override
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    // Only permitted subclass: Rectangle
    public static final class Rectangle extends Shape {
        private final double width;
        private final double height;
        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }
        @Override
        public double area() {
            return width * height;
        }
    }

    public static void main(String[] args) {
        Shape circle = new Circle(2.0);
        Shape rectangle = new Rectangle(3.0, 4.0);
        System.out.println("Circle area: " + circle.area());
        System.out.println("Rectangle area: " + rectangle.area());
    }
}

