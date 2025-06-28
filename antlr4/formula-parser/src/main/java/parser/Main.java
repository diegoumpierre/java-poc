package parser;

import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {
    public static void main(String[] args) throws Exception {
        testeParser("@IDADE == 18 ? 0 : @SALARIO > 1000 ? 1 : @BENEFICIOS < 100 ? 2 : 3 + @BONUS",
                new HashMap<>() {{ // dois colchetes mesmo, não é erro: https://stackoverflow.com/a/6802512
                    put("IDADE", new FormulaValue(18));
                }});
    }

    public static void testeParser(String expressao, Map<String, FormulaValue> map) throws Exception {
        FormulaLexer lexer = new FormulaLexer(CharStreams.fromString(expressao));
        FormulaParser parser = new FormulaParser(new CommonTokenStream(lexer));
        ParseTree tree = parser.formula();
        FormulaEvaluator visitor = new FormulaEvaluator(map);
        System.out.println("Expressão: " + expressao);
        if (map != null) {
            System.out.println("Variáveis: " + map);
        }
        System.out.println("Resultado=" + visitor.visit(tree).toString());
    }
}