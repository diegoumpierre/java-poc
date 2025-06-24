// Generated from /Users/diegoumpierre/Documents/git_diego_umpierre/poc/lunch_and_learn/first-example/src/Lab.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class LabParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		WhiteSpace=10, INT=11, DOUBLE=12, VAR=13, NUM=14, SEMICOLON=15, Operation=16, 
		RelationalOperators=17;
	public static final int
		RULE_start = 0, RULE_ifelse = 1, RULE_if = 2, RULE_condition = 3, RULE_decliration = 4, 
		RULE_int = 5, RULE_double = 6, RULE_multiDecliration = 7, RULE_double_number = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"start", "ifelse", "if", "condition", "decliration", "int", "double", 
			"multiDecliration", "double_number"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'if'", "'('", "')'", "'{'", "'}'", "'else'", "'='", "','", "'.'", 
			null, "'int'", "'double'", null, null, "';'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "WhiteSpace", 
			"INT", "DOUBLE", "VAR", "NUM", "SEMICOLON", "Operation", "RelationalOperators"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Lab.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LabParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StartContext extends ParserRuleContext {
		public DeclirationContext decliration() {
			return getRuleContext(DeclirationContext.class,0);
		}
		public IfContext if_() {
			return getRuleContext(IfContext.class,0);
		}
		public IfelseContext ifelse() {
			return getRuleContext(IfelseContext.class,0);
		}
		public StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterStart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitStart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitStart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StartContext start() throws RecognitionException {
		StartContext _localctx = new StartContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_start);
		try {
			setState(21);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(18);
				decliration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(19);
				if_();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(20);
				ifelse();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfelseContext extends ParserRuleContext {
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public List<DeclirationContext> decliration() {
			return getRuleContexts(DeclirationContext.class);
		}
		public DeclirationContext decliration(int i) {
			return getRuleContext(DeclirationContext.class,i);
		}
		public IfelseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifelse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterIfelse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitIfelse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitIfelse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfelseContext ifelse() throws RecognitionException {
		IfelseContext _localctx = new IfelseContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_ifelse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23);
			match(T__0);
			setState(24);
			match(T__1);
			setState(25);
			condition();
			setState(26);
			match(T__2);
			setState(27);
			match(T__3);
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INT || _la==DOUBLE) {
				{
				{
				setState(28);
				decliration();
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(34);
			match(T__4);
			setState(35);
			match(T__5);
			setState(36);
			match(T__3);
			setState(40);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INT || _la==DOUBLE) {
				{
				{
				setState(37);
				decliration();
				}
				}
				setState(42);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(43);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IfContext extends ParserRuleContext {
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public List<DeclirationContext> decliration() {
			return getRuleContexts(DeclirationContext.class);
		}
		public DeclirationContext decliration(int i) {
			return getRuleContext(DeclirationContext.class,i);
		}
		public IfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitIf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitIf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfContext if_() throws RecognitionException {
		IfContext _localctx = new IfContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_if);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(T__0);
			setState(46);
			match(T__1);
			setState(47);
			condition();
			setState(48);
			match(T__2);
			setState(49);
			match(T__3);
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INT || _la==DOUBLE) {
				{
				{
				setState(50);
				decliration();
				}
				}
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(56);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionContext extends ParserRuleContext {
		public TerminalNode RelationalOperators() { return getToken(LabParser.RelationalOperators, 0); }
		public List<Double_numberContext> double_number() {
			return getRuleContexts(Double_numberContext.class);
		}
		public Double_numberContext double_number(int i) {
			return getRuleContext(Double_numberContext.class,i);
		}
		public List<TerminalNode> NUM() { return getTokens(LabParser.NUM); }
		public TerminalNode NUM(int i) {
			return getToken(LabParser.NUM, i);
		}
		public List<TerminalNode> VAR() { return getTokens(LabParser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(LabParser.VAR, i);
		}
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_condition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(58);
				double_number();
				}
				break;
			case 2:
				{
				setState(59);
				match(NUM);
				}
				break;
			case 3:
				{
				setState(60);
				match(VAR);
				}
				break;
			}
			setState(63);
			match(RelationalOperators);
			setState(67);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(64);
				double_number();
				}
				break;
			case 2:
				{
				setState(65);
				match(NUM);
				}
				break;
			case 3:
				{
				setState(66);
				match(VAR);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclirationContext extends ParserRuleContext {
		public IntContext int_() {
			return getRuleContext(IntContext.class,0);
		}
		public DoubleContext double_() {
			return getRuleContext(DoubleContext.class,0);
		}
		public MultiDeclirationContext multiDecliration() {
			return getRuleContext(MultiDeclirationContext.class,0);
		}
		public DeclirationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decliration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterDecliration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitDecliration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitDecliration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclirationContext decliration() throws RecognitionException {
		DeclirationContext _localctx = new DeclirationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_decliration);
		try {
			setState(72);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(69);
				int_();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(70);
				double_();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(71);
				multiDecliration();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IntContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(LabParser.INT, 0); }
		public List<TerminalNode> VAR() { return getTokens(LabParser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(LabParser.VAR, i);
		}
		public TerminalNode SEMICOLON() { return getToken(LabParser.SEMICOLON, 0); }
		public List<TerminalNode> NUM() { return getTokens(LabParser.NUM); }
		public TerminalNode NUM(int i) {
			return getToken(LabParser.NUM, i);
		}
		public List<TerminalNode> Operation() { return getTokens(LabParser.Operation); }
		public TerminalNode Operation(int i) {
			return getToken(LabParser.Operation, i);
		}
		public IntContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_int; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitInt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitInt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntContext int_() throws RecognitionException {
		IntContext _localctx = new IntContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_int);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			match(INT);
			setState(75);
			match(VAR);
			setState(76);
			match(T__6);
			setState(77);
			_la = _input.LA(1);
			if ( !(_la==VAR || _la==NUM) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Operation) {
				{
				{
				setState(78);
				match(Operation);
				setState(79);
				_la = _input.LA(1);
				if ( !(_la==VAR || _la==NUM) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(84);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(85);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DoubleContext extends ParserRuleContext {
		public TerminalNode DOUBLE() { return getToken(LabParser.DOUBLE, 0); }
		public List<TerminalNode> VAR() { return getTokens(LabParser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(LabParser.VAR, i);
		}
		public TerminalNode SEMICOLON() { return getToken(LabParser.SEMICOLON, 0); }
		public List<Double_numberContext> double_number() {
			return getRuleContexts(Double_numberContext.class);
		}
		public Double_numberContext double_number(int i) {
			return getRuleContext(Double_numberContext.class,i);
		}
		public List<TerminalNode> NUM() { return getTokens(LabParser.NUM); }
		public TerminalNode NUM(int i) {
			return getToken(LabParser.NUM, i);
		}
		public List<TerminalNode> Operation() { return getTokens(LabParser.Operation); }
		public TerminalNode Operation(int i) {
			return getToken(LabParser.Operation, i);
		}
		public DoubleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_double; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterDouble(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitDouble(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitDouble(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DoubleContext double_() throws RecognitionException {
		DoubleContext _localctx = new DoubleContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_double);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(DOUBLE);
			setState(88);
			match(VAR);
			setState(89);
			match(T__6);
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				{
				setState(90);
				double_number();
				}
				break;
			case 2:
				{
				setState(91);
				match(NUM);
				}
				break;
			case 3:
				{
				setState(92);
				match(VAR);
				}
				break;
			}
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Operation) {
				{
				{
				setState(95);
				match(Operation);
				setState(99);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
				case 1:
					{
					setState(96);
					double_number();
					}
					break;
				case 2:
					{
					setState(97);
					match(NUM);
					}
					break;
				case 3:
					{
					setState(98);
					match(VAR);
					}
					break;
				}
				}
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(106);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MultiDeclirationContext extends ParserRuleContext {
		public List<TerminalNode> VAR() { return getTokens(LabParser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(LabParser.VAR, i);
		}
		public TerminalNode SEMICOLON() { return getToken(LabParser.SEMICOLON, 0); }
		public TerminalNode INT() { return getToken(LabParser.INT, 0); }
		public TerminalNode DOUBLE() { return getToken(LabParser.DOUBLE, 0); }
		public MultiDeclirationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiDecliration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterMultiDecliration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitMultiDecliration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitMultiDecliration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiDeclirationContext multiDecliration() throws RecognitionException {
		MultiDeclirationContext _localctx = new MultiDeclirationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_multiDecliration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==DOUBLE) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(109);
			match(VAR);
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(110);
				match(T__7);
				setState(111);
				match(VAR);
				}
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(117);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Double_numberContext extends ParserRuleContext {
		public List<TerminalNode> NUM() { return getTokens(LabParser.NUM); }
		public TerminalNode NUM(int i) {
			return getToken(LabParser.NUM, i);
		}
		public Double_numberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_double_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).enterDouble_number(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabListener ) ((LabListener)listener).exitDouble_number(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabVisitor ) return ((LabVisitor<? extends T>)visitor).visitDouble_number(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Double_numberContext double_number() throws RecognitionException {
		Double_numberContext _localctx = new Double_numberContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_double_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			match(NUM);
			setState(120);
			match(T__8);
			setState(121);
			match(NUM);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0011|\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0001\u0000\u0001\u0000\u0001\u0000\u0003\u0000\u0016\b\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0005\u0001\u001e\b\u0001\n\u0001\f\u0001!\t\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0005\u0001\'\b\u0001\n\u0001\f\u0001*"+
		"\t\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0005\u00024\b\u0002\n\u0002\f\u00027\t"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003>\b\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003D\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004I\b\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0005\u0005Q\b\u0005\n\u0005\f\u0005T\t\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0003\u0006^\b\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0003\u0006d\b\u0006\u0005\u0006f\b\u0006\n\u0006\f\u0006i\t\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005"+
		"\u0007q\b\u0007\n\u0007\f\u0007t\t\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0000\u0000\t\u0000\u0002\u0004\u0006"+
		"\b\n\f\u000e\u0010\u0000\u0002\u0001\u0000\r\u000e\u0001\u0000\u000b\f"+
		"\u0084\u0000\u0015\u0001\u0000\u0000\u0000\u0002\u0017\u0001\u0000\u0000"+
		"\u0000\u0004-\u0001\u0000\u0000\u0000\u0006=\u0001\u0000\u0000\u0000\b"+
		"H\u0001\u0000\u0000\u0000\nJ\u0001\u0000\u0000\u0000\fW\u0001\u0000\u0000"+
		"\u0000\u000el\u0001\u0000\u0000\u0000\u0010w\u0001\u0000\u0000\u0000\u0012"+
		"\u0016\u0003\b\u0004\u0000\u0013\u0016\u0003\u0004\u0002\u0000\u0014\u0016"+
		"\u0003\u0002\u0001\u0000\u0015\u0012\u0001\u0000\u0000\u0000\u0015\u0013"+
		"\u0001\u0000\u0000\u0000\u0015\u0014\u0001\u0000\u0000\u0000\u0016\u0001"+
		"\u0001\u0000\u0000\u0000\u0017\u0018\u0005\u0001\u0000\u0000\u0018\u0019"+
		"\u0005\u0002\u0000\u0000\u0019\u001a\u0003\u0006\u0003\u0000\u001a\u001b"+
		"\u0005\u0003\u0000\u0000\u001b\u001f\u0005\u0004\u0000\u0000\u001c\u001e"+
		"\u0003\b\u0004\u0000\u001d\u001c\u0001\u0000\u0000\u0000\u001e!\u0001"+
		"\u0000\u0000\u0000\u001f\u001d\u0001\u0000\u0000\u0000\u001f \u0001\u0000"+
		"\u0000\u0000 \"\u0001\u0000\u0000\u0000!\u001f\u0001\u0000\u0000\u0000"+
		"\"#\u0005\u0005\u0000\u0000#$\u0005\u0006\u0000\u0000$(\u0005\u0004\u0000"+
		"\u0000%\'\u0003\b\u0004\u0000&%\u0001\u0000\u0000\u0000\'*\u0001\u0000"+
		"\u0000\u0000(&\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)+\u0001"+
		"\u0000\u0000\u0000*(\u0001\u0000\u0000\u0000+,\u0005\u0005\u0000\u0000"+
		",\u0003\u0001\u0000\u0000\u0000-.\u0005\u0001\u0000\u0000./\u0005\u0002"+
		"\u0000\u0000/0\u0003\u0006\u0003\u000001\u0005\u0003\u0000\u000015\u0005"+
		"\u0004\u0000\u000024\u0003\b\u0004\u000032\u0001\u0000\u0000\u000047\u0001"+
		"\u0000\u0000\u000053\u0001\u0000\u0000\u000056\u0001\u0000\u0000\u0000"+
		"68\u0001\u0000\u0000\u000075\u0001\u0000\u0000\u000089\u0005\u0005\u0000"+
		"\u00009\u0005\u0001\u0000\u0000\u0000:>\u0003\u0010\b\u0000;>\u0005\u000e"+
		"\u0000\u0000<>\u0005\r\u0000\u0000=:\u0001\u0000\u0000\u0000=;\u0001\u0000"+
		"\u0000\u0000=<\u0001\u0000\u0000\u0000>?\u0001\u0000\u0000\u0000?C\u0005"+
		"\u0011\u0000\u0000@D\u0003\u0010\b\u0000AD\u0005\u000e\u0000\u0000BD\u0005"+
		"\r\u0000\u0000C@\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000CB\u0001"+
		"\u0000\u0000\u0000D\u0007\u0001\u0000\u0000\u0000EI\u0003\n\u0005\u0000"+
		"FI\u0003\f\u0006\u0000GI\u0003\u000e\u0007\u0000HE\u0001\u0000\u0000\u0000"+
		"HF\u0001\u0000\u0000\u0000HG\u0001\u0000\u0000\u0000I\t\u0001\u0000\u0000"+
		"\u0000JK\u0005\u000b\u0000\u0000KL\u0005\r\u0000\u0000LM\u0005\u0007\u0000"+
		"\u0000MR\u0007\u0000\u0000\u0000NO\u0005\u0010\u0000\u0000OQ\u0007\u0000"+
		"\u0000\u0000PN\u0001\u0000\u0000\u0000QT\u0001\u0000\u0000\u0000RP\u0001"+
		"\u0000\u0000\u0000RS\u0001\u0000\u0000\u0000SU\u0001\u0000\u0000\u0000"+
		"TR\u0001\u0000\u0000\u0000UV\u0005\u000f\u0000\u0000V\u000b\u0001\u0000"+
		"\u0000\u0000WX\u0005\f\u0000\u0000XY\u0005\r\u0000\u0000Y]\u0005\u0007"+
		"\u0000\u0000Z^\u0003\u0010\b\u0000[^\u0005\u000e\u0000\u0000\\^\u0005"+
		"\r\u0000\u0000]Z\u0001\u0000\u0000\u0000][\u0001\u0000\u0000\u0000]\\"+
		"\u0001\u0000\u0000\u0000^g\u0001\u0000\u0000\u0000_c\u0005\u0010\u0000"+
		"\u0000`d\u0003\u0010\b\u0000ad\u0005\u000e\u0000\u0000bd\u0005\r\u0000"+
		"\u0000c`\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000\u0000cb\u0001\u0000"+
		"\u0000\u0000df\u0001\u0000\u0000\u0000e_\u0001\u0000\u0000\u0000fi\u0001"+
		"\u0000\u0000\u0000ge\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000\u0000"+
		"hj\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000jk\u0005\u000f\u0000"+
		"\u0000k\r\u0001\u0000\u0000\u0000lm\u0007\u0001\u0000\u0000mr\u0005\r"+
		"\u0000\u0000no\u0005\b\u0000\u0000oq\u0005\r\u0000\u0000pn\u0001\u0000"+
		"\u0000\u0000qt\u0001\u0000\u0000\u0000rp\u0001\u0000\u0000\u0000rs\u0001"+
		"\u0000\u0000\u0000su\u0001\u0000\u0000\u0000tr\u0001\u0000\u0000\u0000"+
		"uv\u0005\u000f\u0000\u0000v\u000f\u0001\u0000\u0000\u0000wx\u0005\u000e"+
		"\u0000\u0000xy\u0005\t\u0000\u0000yz\u0005\u000e\u0000\u0000z\u0011\u0001"+
		"\u0000\u0000\u0000\f\u0015\u001f(5=CHR]cgr";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}