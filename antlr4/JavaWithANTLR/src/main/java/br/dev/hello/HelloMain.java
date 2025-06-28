package br.dev.hello;

import br.dev.hello.antlrgen.HelloLexer;
import br.dev.hello.antlrgen.HelloParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HelloMain {
    public static void main(String[] args) throws Exception {
        // Create a CharStream that reads from standard input
        System.out.print("Enter a greeting message: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        CharStream input = CharStreams.fromString(line);

        // Create a lexer that feeds off of the input CharStream
        HelloLexer lexer = new HelloLexer(input);

        // Create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create a parser that feeds off the tokens buffer
        HelloParser parser = new HelloParser(tokens);

        // Begin parsing at the 'greeting' rule
        ParseTree tree = parser.greeting();
        // Print the LISP-style tree
        System.out.println("Parse tree: " + tree.toStringTree(parser));
    }


}