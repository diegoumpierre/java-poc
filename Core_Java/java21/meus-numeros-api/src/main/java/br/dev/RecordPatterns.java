package br.dev;

public class RecordPatterns {

    record Point(int x, int y) {}

    static void printSum(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            System.out.println("Sum: " + (x + y));
        }
    }

}
