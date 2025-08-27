package main.java.br.dev;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class NashornEngineExample {


    public static void main(String[] args) throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.put("message", "Hello from Java!");
        engine.eval("print(message);"); // Output: Hello from Java!

        // More complex example: define and call a JS function from Java
        String jsFunction = "function add(a, b) { return a + b; }";
        engine.eval(jsFunction);
        Invocable invocable = (Invocable) engine;
        Object result = invocable.invokeFunction("add", 10, 20);
        System.out.println("Result of add(10, 20) from JS: " + result); // Output: 30

        // Even more complex: JS function using Java object
        engine.put("javaObj", new Helper());
        String jsWithJava = "function callJavaMethod(x) { return javaObj.square(x); }";
        engine.eval(jsWithJava);
        Object squareResult = invocable.invokeFunction("callJavaMethod", 7);
        System.out.println("Result of callJavaMethod(7): " + squareResult); // Output: 49
    }

    // Helper class to be used from JavaScript
    public static class Helper {
        public int square(int x) {
            return x * x;
        }
    }
}
