package br.dev.java8;

import br.dev.java8.antlrgen.Java8Lexer;
import br.dev.java8.antlrgen.Java8Parser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class UppercaseMethodListenerTest {

    @Test
    void testMyJava() {
        String javaClassContent = "public class SampleClass { void DoSomething(){} }";

        Java8Lexer java8Lexer = new Java8Lexer(CharStreams.fromString(javaClassContent));

        CommonTokenStream tokens = new CommonTokenStream(java8Lexer);

        Java8Parser parser = new Java8Parser(tokens);

        ParseTree tree = parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();

        UppercaseMethodListener listener= new UppercaseMethodListener();

        walker.walk(listener, tree);

        assertThat(listener.getErrors().size()).isEqualTo(1);
        assertThat(listener.getErrors().get(0)).isEqualTo("Method DoSomething is uppercased!");
    }
}