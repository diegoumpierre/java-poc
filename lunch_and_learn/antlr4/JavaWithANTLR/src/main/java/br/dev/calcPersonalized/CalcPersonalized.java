package br.dev.calcPersonalized;

import br.dev.calcPersonalized.antlrgen.CalcPersonalizedLexer;
import br.dev.calcPersonalized.antlrgen.CalcPersonalizedParser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class CalcPersonalized {

    public static void main(String[] args) throws Exception {
        String input = "x = 4 + 5 \n y = x * 2 \n z = x $$ y \n z";
        CalcPersonalizedLexer lexer = new CalcPersonalizedLexer(CharStreams.fromString(input));
        CalcPersonalizedParser parser = new CalcPersonalizedParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.prog();

        MyPersonalVisitor visitor = new MyPersonalVisitor();
        Integer result = visitor.visit(tree);

        System.out.println("Result = " + result);  // Should print: Result = 18
    }



}
