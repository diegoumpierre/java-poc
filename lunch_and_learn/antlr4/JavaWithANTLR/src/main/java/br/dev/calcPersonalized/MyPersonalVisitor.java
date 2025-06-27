package br.dev.calcPersonalized;

import br.dev.calcPersonalized.antlrgen.CalcPersonalizedBaseVisitor;
import br.dev.calcPersonalized.antlrgen.CalcPersonalizedParser;

import java.util.HashMap;
import java.util.Map;

public class MyPersonalVisitor extends CalcPersonalizedBaseVisitor<Integer> {
    Map<String, Integer> memory = new HashMap<>();

    @Override
    public Integer visitAssign(CalcPersonalizedParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        int value = visit(ctx.expr());
        memory.put(id, value);
        return value;
    }

    @Override
    public Integer visitExprOnly(CalcPersonalizedParser.ExprOnlyContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Integer visitAddExpr(CalcPersonalizedParser.AddExprContext ctx) {
        return visit(ctx.expr(0)) + visit(ctx.expr(1));
    }

    @Override
    public Integer visitMulExpr(CalcPersonalizedParser.MulExprContext ctx) {
        return visit(ctx.expr(0)) * visit(ctx.expr(1));
    }

    @Override
    public Integer visitIntExpr(CalcPersonalizedParser.IntExprContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
    }

    @Override
    public Integer visitVarExpr(CalcPersonalizedParser.VarExprContext ctx) {
        String id = ctx.ID().getText();
        return memory.getOrDefault(id, 0);
    }

    @Override
    public Integer visitParenExpr(CalcPersonalizedParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Integer visitUmpierre(CalcPersonalizedParser.UmpierreContext ctx) {
        int currentValue = visit(ctx.getChild(0)); //9
        currentValue = currentValue * 1000; //9000
        int otherValue = visit(ctx.getChild(2));
        currentValue += otherValue;

        return currentValue;
    }

}
