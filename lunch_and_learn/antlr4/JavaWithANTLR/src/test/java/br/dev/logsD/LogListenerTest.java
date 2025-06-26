package br.dev.logsD;

import br.dev.logsD.antlrgen.LogsLexer;
import br.dev.logsD.antlrgen.LogsParser;
import jdk.jfr.internal.LogLevel;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


class LogListenerTest {

    @Test
    public void whenLogContainsOneErrorLogEntry_thenOneErrorIsReturned() throws Exception {

        String logLine ="2025-Jun-26 14:20:24 ERROR Good thing happened" +
                "2025-Jun-26 20:20:18 INFO number error\n" +
                "2025-May-05 20:20:19 INFO some method started\n" +
                "2025-May-05 20:20:20 INFO error in method\n" +
                "2025-May-05 20:20:21 DEBUG error executed integration\n" +
                "2025-May-05 20:20:21 DEBUG move to new folder\n" +
                "2025-May-05 20:20:24 ERROR cant connect to a database" ;
        LogsLexer logsLexer = new LogsLexer(CharStreams.fromString(logLine));
        CommonTokenStream tokens = new CommonTokenStream(logsLexer);
        LogsParser logParser = new LogsParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        LogListener listener= new LogListener();

        walker.walk(listener, logParser.log());
        LogEntry entry = listener.getEntries().get(0);

        assertThat(entry.getLevel()).isEqualTo(LogLevel.ERROR);
        assertThat(entry.getMessage()).isEqualTo("Good thing happened");
        assertThat(entry.getTimestamp()).isEqualTo((LocalDateTime.of(2025,6,26,14,20,24)));
    }


}