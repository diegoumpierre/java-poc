package br.dev;

import antlrgen.Java8BaseListener;
import antlrgen.Java8Parser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class UppercaseMethodListener extends Java8BaseListener {

    private List<String> errors = new ArrayList<>();

    // ... getter for errors

    @Override
    public void enterMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        TerminalNode node = ctx.Identifier();
        String methodName = node.getText();

        if (Character.isUpperCase(methodName.charAt(0))) {
            String error = String.format("Method %s is uppercased!", methodName);
            errors.add(error);
        }
    }

    public List<String> getErrors() {
            return this.errors;
    }
}