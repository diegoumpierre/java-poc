package br.dev.calcPersonalized.antlrgen;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CalcPersonalizedParser}.
 */
public interface CalcPersonalizedListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CalcPersonalizedParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(CalcPersonalizedParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link CalcPersonalizedParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(CalcPersonalizedParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link CalcPersonalizedParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterAssign(CalcPersonalizedParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link CalcPersonalizedParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitAssign(CalcPersonalizedParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprOnly}
	 * labeled alternative in {@link CalcPersonalizedParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterExprOnly(CalcPersonalizedParser.ExprOnlyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprOnly}
	 * labeled alternative in {@link CalcPersonalizedParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitExprOnly(CalcPersonalizedParser.ExprOnlyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulExpr(CalcPersonalizedParser.MulExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulExpr(CalcPersonalizedParser.MulExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Umpierre}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUmpierre(CalcPersonalizedParser.UmpierreContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Umpierre}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUmpierre(CalcPersonalizedParser.UmpierreContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VarExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVarExpr(CalcPersonalizedParser.VarExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVarExpr(CalcPersonalizedParser.VarExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(CalcPersonalizedParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(CalcPersonalizedParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IntExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterIntExpr(CalcPersonalizedParser.IntExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IntExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitIntExpr(CalcPersonalizedParser.IntExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(CalcPersonalizedParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(CalcPersonalizedParser.ParenExprContext ctx);
}