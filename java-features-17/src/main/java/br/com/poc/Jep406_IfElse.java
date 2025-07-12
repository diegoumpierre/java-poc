package br.com.poc;

public class Jep406_IfElse {
    // IT IS A PREVIEW ON JAVA 17 !!!!
}
class beforeJava17{
    public static void main(String[] args) {

        System.out.println(formatter("Java 17"));
        System.out.println(formatter(17));

    }

    static String formatter(Object o) {
        String formatted = "unknown";
        if (o instanceof Integer i) {
            formatted = String.format("int %d", i);
        } else if (o instanceof Long l) {
            formatted = String.format("long %d", l);
        } else if (o instanceof Double d) {
            formatted = String.format("double %f", d);
        } else if (o instanceof String s) {
            formatted = String.format("String %s", s);
        }
        return formatted;
    }
}

class afterJava17{
    public static void main(String[] args) {

        System.out.println(formatterJava17("Java 17"));
        System.out.println(formatterJava17(17));

    }

    static String formatterJava17(Object o) {
        return switch (o) {
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case Double d  -> String.format("double %f", d);
            case String s  -> String.format("String %s", s);
            default        -> o.toString();
        };
    }

}
