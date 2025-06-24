
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class CalcCompiler {
    public static void main(String[] args) throws Exception {
        String input = "a = 5\nb = 6\na + b * 2\n";

        CharStream charStream = CharStreams.fromString(input);
        CalcLexer lexer = new CalcLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CalcParser parser = new CalcParser(tokens);

        ParseTree tree = parser.prog();

        EvalVisitor eval = new EvalVisitor();
        eval.visit(tree);
    }
}
