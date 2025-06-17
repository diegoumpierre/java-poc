package br.dev;

public class PatternMatchingForSwitch {


    static String formatter(Object obj) {
        return switch (obj) {
            case Integer i -> "int " + i;
            case String s when s.length() > 5 -> "long string " + s;
            case String s -> "short string " + s;
            default -> "unknown";
        };


    }
}
