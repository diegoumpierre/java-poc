// Generated from /Users/diegoumpierre/Documents/git_diego_umpierre/poc/lunch_and_learn/antlr4/sql-example/sql.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SqlLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		SELECT=1, FROM=2, WHERE=3, AND=4, STAR=5, COMMA=6, EQUALS=7, NOTEQ=8, 
		LT=9, GT=10, LE=11, GE=12, IDENTIFIER=13, STRING=14, NUMBER=15, WS=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"SELECT", "FROM", "WHERE", "AND", "STAR", "COMMA", "EQUALS", "NOTEQ", 
			"LT", "GT", "LE", "GE", "IDENTIFIER", "STRING", "NUMBER", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'SELECT'", "'FROM'", "'WHERE'", "'AND'", "'*'", "','", "'='", 
			null, "'<'", "'>'", "'<='", "'>='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "SELECT", "FROM", "WHERE", "AND", "STAR", "COMMA", "EQUALS", "NOTEQ", 
			"LT", "GT", "LE", "GE", "IDENTIFIER", "STRING", "NUMBER", "WS"
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


	public SqlLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "sql.g4"; }

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
		"\u0004\u0000\u0010q\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007B\b\u0007\u0001"+
		"\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\f\u0001\f\u0005\fP\b\f\n\f\f\fS\t\f\u0001\r\u0001\r"+
		"\u0005\rW\b\r\n\r\f\rZ\t\r\u0001\r\u0001\r\u0001\u000e\u0004\u000e_\b"+
		"\u000e\u000b\u000e\f\u000e`\u0001\u000e\u0001\u000e\u0004\u000ee\b\u000e"+
		"\u000b\u000e\f\u000ef\u0003\u000ei\b\u000e\u0001\u000f\u0004\u000fl\b"+
		"\u000f\u000b\u000f\f\u000fm\u0001\u000f\u0001\u000f\u0000\u0000\u0010"+
		"\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r"+
		"\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e"+
		"\u001d\u000f\u001f\u0010\u0001\u0000\u0005\u0003\u0000AZ__az\u0004\u0000"+
		"09AZ__az\u0003\u0000\n\n\r\r\'\'\u0001\u000009\u0003\u0000\t\n\r\r  w"+
		"\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000"+
		"\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000"+
		"\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000"+
		"\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0001!\u0001"+
		"\u0000\u0000\u0000\u0003(\u0001\u0000\u0000\u0000\u0005-\u0001\u0000\u0000"+
		"\u0000\u00073\u0001\u0000\u0000\u0000\t7\u0001\u0000\u0000\u0000\u000b"+
		"9\u0001\u0000\u0000\u0000\r;\u0001\u0000\u0000\u0000\u000fA\u0001\u0000"+
		"\u0000\u0000\u0011C\u0001\u0000\u0000\u0000\u0013E\u0001\u0000\u0000\u0000"+
		"\u0015G\u0001\u0000\u0000\u0000\u0017J\u0001\u0000\u0000\u0000\u0019M"+
		"\u0001\u0000\u0000\u0000\u001bT\u0001\u0000\u0000\u0000\u001d^\u0001\u0000"+
		"\u0000\u0000\u001fk\u0001\u0000\u0000\u0000!\"\u0005S\u0000\u0000\"#\u0005"+
		"E\u0000\u0000#$\u0005L\u0000\u0000$%\u0005E\u0000\u0000%&\u0005C\u0000"+
		"\u0000&\'\u0005T\u0000\u0000\'\u0002\u0001\u0000\u0000\u0000()\u0005F"+
		"\u0000\u0000)*\u0005R\u0000\u0000*+\u0005O\u0000\u0000+,\u0005M\u0000"+
		"\u0000,\u0004\u0001\u0000\u0000\u0000-.\u0005W\u0000\u0000./\u0005H\u0000"+
		"\u0000/0\u0005E\u0000\u000001\u0005R\u0000\u000012\u0005E\u0000\u0000"+
		"2\u0006\u0001\u0000\u0000\u000034\u0005A\u0000\u000045\u0005N\u0000\u0000"+
		"56\u0005D\u0000\u00006\b\u0001\u0000\u0000\u000078\u0005*\u0000\u0000"+
		"8\n\u0001\u0000\u0000\u00009:\u0005,\u0000\u0000:\f\u0001\u0000\u0000"+
		"\u0000;<\u0005=\u0000\u0000<\u000e\u0001\u0000\u0000\u0000=>\u0005!\u0000"+
		"\u0000>B\u0005=\u0000\u0000?@\u0005<\u0000\u0000@B\u0005>\u0000\u0000"+
		"A=\u0001\u0000\u0000\u0000A?\u0001\u0000\u0000\u0000B\u0010\u0001\u0000"+
		"\u0000\u0000CD\u0005<\u0000\u0000D\u0012\u0001\u0000\u0000\u0000EF\u0005"+
		">\u0000\u0000F\u0014\u0001\u0000\u0000\u0000GH\u0005<\u0000\u0000HI\u0005"+
		"=\u0000\u0000I\u0016\u0001\u0000\u0000\u0000JK\u0005>\u0000\u0000KL\u0005"+
		"=\u0000\u0000L\u0018\u0001\u0000\u0000\u0000MQ\u0007\u0000\u0000\u0000"+
		"NP\u0007\u0001\u0000\u0000ON\u0001\u0000\u0000\u0000PS\u0001\u0000\u0000"+
		"\u0000QO\u0001\u0000\u0000\u0000QR\u0001\u0000\u0000\u0000R\u001a\u0001"+
		"\u0000\u0000\u0000SQ\u0001\u0000\u0000\u0000TX\u0005\'\u0000\u0000UW\b"+
		"\u0002\u0000\u0000VU\u0001\u0000\u0000\u0000WZ\u0001\u0000\u0000\u0000"+
		"XV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000Y[\u0001\u0000\u0000"+
		"\u0000ZX\u0001\u0000\u0000\u0000[\\\u0005\'\u0000\u0000\\\u001c\u0001"+
		"\u0000\u0000\u0000]_\u0007\u0003\u0000\u0000^]\u0001\u0000\u0000\u0000"+
		"_`\u0001\u0000\u0000\u0000`^\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000"+
		"\u0000ah\u0001\u0000\u0000\u0000bd\u0005.\u0000\u0000ce\u0007\u0003\u0000"+
		"\u0000dc\u0001\u0000\u0000\u0000ef\u0001\u0000\u0000\u0000fd\u0001\u0000"+
		"\u0000\u0000fg\u0001\u0000\u0000\u0000gi\u0001\u0000\u0000\u0000hb\u0001"+
		"\u0000\u0000\u0000hi\u0001\u0000\u0000\u0000i\u001e\u0001\u0000\u0000"+
		"\u0000jl\u0007\u0004\u0000\u0000kj\u0001\u0000\u0000\u0000lm\u0001\u0000"+
		"\u0000\u0000mk\u0001\u0000\u0000\u0000mn\u0001\u0000\u0000\u0000no\u0001"+
		"\u0000\u0000\u0000op\u0006\u000f\u0000\u0000p \u0001\u0000\u0000\u0000"+
		"\b\u0000AQX`fhm\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}