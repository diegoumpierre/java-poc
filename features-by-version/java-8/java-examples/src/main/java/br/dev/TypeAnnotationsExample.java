package main.java.br.dev;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Target({ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@interface NonNull {
}

public class TypeAnnotationsExample {
    public static void main(String[] args) {

        // Local variable annotation
        @NonNull String name = "Diego";

        // Generic type argument
        List<@NonNull String> names = new ArrayList<>();
        names.add("Adriano");
        names.add("Alice");

        // Type cast
        // Object result = getSomething();
        // String name = (@NonNull String) result;
        String string = (@NonNull String) "Hello World";



        // Array type use
        String @NonNull [] items = {"One", "Two"};

        System.out.println("Names: " + names);
        new TypeAnnotationsExample().printArray(items);
    }

    public String process(@NonNull String input) {
        return input.trim();
    }

    public void printArray(String @NonNull [] items) {
        for (String item : items) {
            System.out.println(item);
        }
    }


}
