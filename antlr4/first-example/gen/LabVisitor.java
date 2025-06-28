// Generated from /Users/diegoumpierre/Documents/git_diego_umpierre/poc/lunch_and_learn/first-example/src/Lab.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LabParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LabVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LabParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(LabParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#ifelse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfelse(LabParser.IfelseContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#if}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(LabParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(LabParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#decliration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecliration(LabParser.DeclirationContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt(LabParser.IntContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#double}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDouble(LabParser.DoubleContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#multiDecliration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiDecliration(LabParser.MultiDeclirationContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabParser#double_number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDouble_number(LabParser.Double_numberContext ctx);
}