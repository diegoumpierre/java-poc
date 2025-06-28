
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class CalcToJava {
    public static void main(String[] args) throws Exception {
        String input = "a = 5\nb = 6\na + b * 2\n";

        CharStream charStream = CharStreams.fromString(input);
        CalcLexer lexer = new CalcLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CalcParser parser = new CalcParser(tokens);

        ParseTree tree = parser.prog();

        JavaTranspiler transpiler = new JavaTranspiler();
        String javaCode = transpiler.visit(tree);

        System.out.println("// Generated Java Code:");
        System.out.println("public class GeneratedCode {");
        System.out.println("    public static void main(String[] args) {");
        System.out.println("        int a, b;");
        System.out.println("        " + javaCode);
        System.out.println("    }");
        System.out.println("}");
    }
}
