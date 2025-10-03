package br.dev;

// Hypothetical Java 25 value class syntax
public value class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    // No identity, only value-based equality
    @Override
    public boolean equals(Object o) {
        return (o instanceof Point p) && p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) * 31 + Integer.hashCode(y);
    }

    @Override
    public String toString() {
        return "Point[" + x + ", " + y + "]";
    }
}

class ValueObjectExample {
    public static void main(String[] args) {
        Point p1 = new Point(3, 4);
        Point p2 = new Point(3, 4);
        Point p3 = new Point(5, 6);

        System.out.println(p1); // Point[3, 4]
        System.out.println(p2); // Point[3, 4]
        System.out.println(p3); // Point[5, 6]
        System.out.println("p1 == p2: " + (p1 == p2)); // true (value equality)
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true
        System.out.println("p1 == p3: " + (p1 == p3)); // false
    }
}

