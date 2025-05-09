package java;

import java.gen.LabeledExprLexer;
import java.gen.LabeledExprParser;
import java.io.FileInputStream;
import java.io.InputStream;

public class CalcByVisit {

    public static void main(String[] args) throws Exception {
        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        ANTLRInputStream input = new ANTLRInputStream("1+2*3\n");
        LabeledExprLexer lexer = new LabeledExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LabeledExprParser parser = new LabeledExprParser(tokens);
        ParseTree tree = parser.prog(); // parse
        EvalVisitor eval = new EvalVisitor();
        int result =eval.visit(tree);
        System.out.println(result);
    }


}
