// Generated from /Users/diegoumpierre/Documents/git_diego_umpierre/poc/lunch_and_learn/first-example/src/Lab.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LabParser}.
 */
public interface LabListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LabParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(LabParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(LabParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#ifelse}.
	 * @param ctx the parse tree
	 */
	void enterIfelse(LabParser.IfelseContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#ifelse}.
	 * @param ctx the parse tree
	 */
	void exitIfelse(LabParser.IfelseContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#if}.
	 * @param ctx the parse tree
	 */
	void enterIf(LabParser.IfContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#if}.
	 * @param ctx the parse tree
	 */
	void exitIf(LabParser.IfContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(LabParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(LabParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#decliration}.
	 * @param ctx the parse tree
	 */
	void enterDecliration(LabParser.DeclirationContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#decliration}.
	 * @param ctx the parse tree
	 */
	void exitDecliration(LabParser.DeclirationContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#int}.
	 * @param ctx the parse tree
	 */
	void enterInt(LabParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#int}.
	 * @param ctx the parse tree
	 */
	void exitInt(LabParser.IntContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#double}.
	 * @param ctx the parse tree
	 */
	void enterDouble(LabParser.DoubleContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#double}.
	 * @param ctx the parse tree
	 */
	void exitDouble(LabParser.DoubleContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#multiDecliration}.
	 * @param ctx the parse tree
	 */
	void enterMultiDecliration(LabParser.MultiDeclirationContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#multiDecliration}.
	 * @param ctx the parse tree
	 */
	void exitMultiDecliration(LabParser.MultiDeclirationContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabParser#double_number}.
	 * @param ctx the parse tree
	 */
	void enterDouble_number(LabParser.Double_numberContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabParser#double_number}.
	 * @param ctx the parse tree
	 */
	void exitDouble_number(LabParser.Double_numberContext ctx);
}