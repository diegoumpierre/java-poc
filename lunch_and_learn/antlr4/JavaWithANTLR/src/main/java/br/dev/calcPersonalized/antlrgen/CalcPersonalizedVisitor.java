package br.dev.calcPersonalized.antlrgen;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CalcPersonalizedParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CalcPersonalizedVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CalcPersonalizedParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(CalcPersonalizedParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link CalcPersonalizedParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(CalcPersonalizedParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprOnly}
	 * labeled alternative in {@link CalcPersonalizedParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprOnly(CalcPersonalizedParser.ExprOnlyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MulExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExpr(CalcPersonalizedParser.MulExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Umpierre}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUmpierre(CalcPersonalizedParser.UmpierreContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarExpr(CalcPersonalizedParser.VarExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(CalcPersonalizedParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntExpr(CalcPersonalizedParser.IntExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link CalcPersonalizedParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(CalcPersonalizedParser.ParenExprContext ctx);
}