// Generated from /Users/diegoumpierre/Documents/git_diego_umpierre/poc/lunch_and_learn/first-example/src/Lab.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class LabLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		WhiteSpace=10, INT=11, DOUBLE=12, VAR=13, NUM=14, SEMICOLON=15, Operation=16, 
		RelationalOperators=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"WhiteSpace", "INT", "DOUBLE", "VAR", "NUM", "SEMICOLON", "Operation", 
			"RelationalOperators"
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


	public LabLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Lab.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0011f\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\t\u0004\t;\b\t\u000b\t\f\t<\u0001\t\u0001\t\u0001\n\u0001"+
		"\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0005\fM\b\f\n\f\f\fP\t\f\u0001"+
		"\r\u0005\rS\b\r\n\r\f\rV\t\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001"+
		"\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010e\b\u0010\u0000"+
		"\u0000\u0011\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b"+
		"\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b"+
		"\u000e\u001d\u000f\u001f\u0010!\u0011\u0001\u0000\u0004\u0003\u0000\t"+
		"\n\r\r  \u0002\u0000AZaz\u0003\u0000*+--//\u0002\u0000<<>>l\u0000\u0001"+
		"\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005"+
		"\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001"+
		"\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000"+
		"\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000"+
		"\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000"+
		"\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000"+
		"\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000"+
		"\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000"+
		"\u0000\u0001#\u0001\u0000\u0000\u0000\u0003&\u0001\u0000\u0000\u0000\u0005"+
		"(\u0001\u0000\u0000\u0000\u0007*\u0001\u0000\u0000\u0000\t,\u0001\u0000"+
		"\u0000\u0000\u000b.\u0001\u0000\u0000\u0000\r3\u0001\u0000\u0000\u0000"+
		"\u000f5\u0001\u0000\u0000\u0000\u00117\u0001\u0000\u0000\u0000\u0013:"+
		"\u0001\u0000\u0000\u0000\u0015@\u0001\u0000\u0000\u0000\u0017D\u0001\u0000"+
		"\u0000\u0000\u0019N\u0001\u0000\u0000\u0000\u001bT\u0001\u0000\u0000\u0000"+
		"\u001dW\u0001\u0000\u0000\u0000\u001fY\u0001\u0000\u0000\u0000!d\u0001"+
		"\u0000\u0000\u0000#$\u0005i\u0000\u0000$%\u0005f\u0000\u0000%\u0002\u0001"+
		"\u0000\u0000\u0000&\'\u0005(\u0000\u0000\'\u0004\u0001\u0000\u0000\u0000"+
		"()\u0005)\u0000\u0000)\u0006\u0001\u0000\u0000\u0000*+\u0005{\u0000\u0000"+
		"+\b\u0001\u0000\u0000\u0000,-\u0005}\u0000\u0000-\n\u0001\u0000\u0000"+
		"\u0000./\u0005e\u0000\u0000/0\u0005l\u0000\u000001\u0005s\u0000\u0000"+
		"12\u0005e\u0000\u00002\f\u0001\u0000\u0000\u000034\u0005=\u0000\u0000"+
		"4\u000e\u0001\u0000\u0000\u000056\u0005,\u0000\u00006\u0010\u0001\u0000"+
		"\u0000\u000078\u0005.\u0000\u00008\u0012\u0001\u0000\u0000\u00009;\u0007"+
		"\u0000\u0000\u0000:9\u0001\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000"+
		"<:\u0001\u0000\u0000\u0000<=\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000"+
		"\u0000>?\u0006\t\u0000\u0000?\u0014\u0001\u0000\u0000\u0000@A\u0005i\u0000"+
		"\u0000AB\u0005n\u0000\u0000BC\u0005t\u0000\u0000C\u0016\u0001\u0000\u0000"+
		"\u0000DE\u0005d\u0000\u0000EF\u0005o\u0000\u0000FG\u0005u\u0000\u0000"+
		"GH\u0005b\u0000\u0000HI\u0005l\u0000\u0000IJ\u0005e\u0000\u0000J\u0018"+
		"\u0001\u0000\u0000\u0000KM\u0007\u0001\u0000\u0000LK\u0001\u0000\u0000"+
		"\u0000MP\u0001\u0000\u0000\u0000NL\u0001\u0000\u0000\u0000NO\u0001\u0000"+
		"\u0000\u0000O\u001a\u0001\u0000\u0000\u0000PN\u0001\u0000\u0000\u0000"+
		"QS\u000209\u0000RQ\u0001\u0000\u0000\u0000SV\u0001\u0000\u0000\u0000T"+
		"R\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000U\u001c\u0001\u0000"+
		"\u0000\u0000VT\u0001\u0000\u0000\u0000WX\u0005;\u0000\u0000X\u001e\u0001"+
		"\u0000\u0000\u0000YZ\u0007\u0002\u0000\u0000Z \u0001\u0000\u0000\u0000"+
		"[\\\u0005=\u0000\u0000\\e\u0005=\u0000\u0000]^\u0005!\u0000\u0000^e\u0005"+
		"=\u0000\u0000_e\u0007\u0003\u0000\u0000`a\u0005>\u0000\u0000ae\u0005="+
		"\u0000\u0000bc\u0005<\u0000\u0000ce\u0005=\u0000\u0000d[\u0001\u0000\u0000"+
		"\u0000d]\u0001\u0000\u0000\u0000d_\u0001\u0000\u0000\u0000d`\u0001\u0000"+
		"\u0000\u0000db\u0001\u0000\u0000\u0000e\"\u0001\u0000\u0000\u0000\u0005"+
		"\u0000<NTd\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}