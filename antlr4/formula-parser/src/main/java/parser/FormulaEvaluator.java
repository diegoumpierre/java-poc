package parser;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FormulaEvaluator extends FormulaBaseVisitor<FormulaValue> {
    // Map que simula os valores já lidos do banco de dados
    private Map<String, FormulaValue> bd = new HashMap<>();
    public FormulaEvaluator(Map<String, FormulaValue> bd) {
        this.bd = bd;
    }

    @Override
    public FormulaValue visitFormula(FormulaParser.FormulaContext ctx) {
        return this.visit(ctx.expr());
    }

    @Override
    public FormulaValue visitTernaryExpr(FormulaParser.TernaryExprContext ctx) {
        FormulaValue condition = this.visit(ctx.expr(0));
        return condition.booleanValue() ? this.visit(ctx.expr(1)) : this.visit(ctx.expr(2));
    }

    @Override
    public FormulaValue visitIdAtom(FormulaParser.IdAtomContext ctx) {
        String id = ctx.NOME().getText();
        System.out.printf("Procurando variável %s\n", id);
        if (bd.containsKey(id)) { // a busca no Map simula a query sendo executada no banco
            return bd.get(id);
        }
        throw new IllegalArgumentException("Valor " + id + " não encontrado");
    }

    @Override
    public FormulaValue visitNumberAtom(FormulaParser.NumberAtomContext ctx) {
        BigDecimal number = new BigDecimal(ctx.getText());
        boolean isNotZero = number.compareTo(BigDecimal.ZERO) != 0;
        return new FormulaValue(isNotZero, number);
    }

    @Override
    public FormulaValue visitParenthesisAtom(FormulaParser.ParenthesisAtomContext ctx) {
        return this.visit(ctx.expr()); // expressão entre parênteses, retorna o valor de toda a expressão
    }

    @Override
    public FormulaValue visitMultiplicationExpr(FormulaParser.MultiplicationExprContext ctx) {
        FormulaValue left = this.visit(ctx.expr(0));
        FormulaValue right = this.visit(ctx.expr(1));
        switch (ctx.op.getType()) {
            case FormulaParser.MULT: // multiplicação
                return new FormulaValue(left.numberValue().multiply(right.numberValue()));
            case FormulaParser.DIV: // divisão
                return new FormulaValue(left.numberValue().divide(right.numberValue()));
            default:
                throw new RuntimeException("unknown operator: " + FormulaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaValue visitAdditiveExpr(FormulaParser.AdditiveExprContext ctx) {
        FormulaValue left = this.visit(ctx.expr(0));
        FormulaValue right = this.visit(ctx.expr(1));
        switch (ctx.op.getType()) {
            case FormulaParser.PLUS: // adição
                return new FormulaValue(left.numberValue().add(right.numberValue()));
            case FormulaParser.MINUS: // subtração
                return new FormulaValue(left.numberValue().subtract(right.numberValue()));
            default:
                throw new RuntimeException("unknown operator: " + FormulaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaValue visitRelationalExpr(FormulaParser.RelationalExprContext ctx) {
        FormulaValue left = this.visit(ctx.expr(0));
        FormulaValue right = this.visit(ctx.expr(1));
        switch (ctx.op.getType()) {
            case FormulaParser.LT: // menor
                return new FormulaValue(left.numberValue().compareTo(right.numberValue()) < 0);
            case FormulaParser.LTEQ: // menor ou igual
                return new FormulaValue(left.numberValue().compareTo(right.numberValue()) <= 0);
            case FormulaParser.GT: // maior
                return new FormulaValue(left.numberValue().compareTo(right.numberValue()) > 0);
            case FormulaParser.GTEQ: // maior ou igual
                return new FormulaValue(left.numberValue().compareTo(right.numberValue()) >= 0);
            default:
                throw new RuntimeException("unknown operator: " + FormulaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaValue visitEqualityExpr(FormulaParser.EqualityExprContext ctx) {
        FormulaValue left = this.visit(ctx.expr(0));
        FormulaValue right = this.visit(ctx.expr(1));
        switch (ctx.op.getType()) {
            case FormulaParser.EQ: // igual
                return new FormulaValue(left.numberValue().compareTo(right.numberValue()) == 0);
            case FormulaParser.NEQ: // diferente
                return new FormulaValue(left.numberValue().compareTo(right.numberValue()) != 0);
            default:
                throw new RuntimeException("unknown operator: " + FormulaParser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public FormulaValue visitAndExpr(FormulaParser.AndExprContext ctx) {
        FormulaValue left = this.visit(ctx.expr(0));
        if (left.booleanValue()) { // se o primeiro é verdadeiro, avalia o segundo
            return new FormulaValue(this.visit(ctx.expr(1)).booleanValue());
        }
        return new FormulaValue(false); // se o primeiro é falso, o resultado com certeza é falso
    }

    @Override
    public FormulaValue visitOrExpr(FormulaParser.OrExprContext ctx) {
        FormulaValue left = this.visit(ctx.expr(0));
        if (left.booleanValue()) { // se o primeiro é verdadeiro, não precisa avaliar o segundo
            return new FormulaValue(true);
        }
        return new FormulaValue(this.visit(ctx.expr(1)).booleanValue()); // o primeiro é falso, avalia o segundo
    }
}