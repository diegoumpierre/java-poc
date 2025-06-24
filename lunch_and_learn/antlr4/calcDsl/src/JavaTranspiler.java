
import java.util.HashMap;
import java.util.Map;

public class JavaTranspiler extends CalcBaseVisitor<String> {
    Map<String, String> memory = new HashMap<>();

    @Override
    public String visitAssign(CalcParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        String value = visit(ctx.expr());
        memory.put(id, value);
        return id + " = " + value + ";";
    }

    @Override
    public String visitPrintExpr(CalcParser.PrintExprContext ctx) {
        String value = visit(ctx.expr());
        return "System.out.println(" + value + ");";
    }

    @Override
    public String visitInt(CalcParser.IntContext ctx) {
        return ctx.INT().getText();
    }

    @Override
    public String visitId(CalcParser.IdContext ctx) {
        return ctx.ID().getText();
    }

    @Override
    public String visitMulDiv(CalcParser.MulDivContext ctx) {
        String left = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        String op = ctx.op.getText();
        return "(" + left + " " + op + " " + right + ")";
    }

    @Override
    public String visitAddSub(CalcParser.AddSubContext ctx) {
        String left = visit(ctx.expr(0));
        String right = visit(ctx.expr(1));
        String op = ctx.op.getText();
        return "(" + left + " " + op + " " + right + ")";
    }

    @Override
    public String visitParens(CalcParser.ParensContext ctx) {
        return "(" + visit(ctx.expr()) + ")";
    }
}
