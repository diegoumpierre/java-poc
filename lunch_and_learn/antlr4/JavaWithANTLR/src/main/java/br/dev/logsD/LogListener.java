package br.dev.logsD;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import br.dev.logsD.antlrgen.LogsBaseListener;
import br.dev.logsD.antlrgen.LogsParser;
import jdk.jfr.internal.LogLevel;

public class LogListener extends LogsBaseListener {

    private List<LogEntry> entries = new ArrayList<>();
    private LogEntry current;

    public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss"); //2025-Jun-26 20:20:18

    @Override public void enterEntry(LogsParser.EntryContext ctx) {
        this.current = new LogEntry();
    }

    @Override
    public void enterTimestamp(LogsParser.TimestampContext ctx){
        this.current.setTimestamp(
                LocalDateTime.parse(ctx.getText(), DEFAULT_DATETIME_FORMATTER));
    };


    @Override public void enterMessage(LogsParser.MessageContext ctx) {
        this.current.setMessage(ctx.getText());
    }

    @Override public void enterLevel(LogsParser.LevelContext ctx) {
        this.current.setLevel(LogLevel.valueOf(ctx.getText()));
    }

    @Override public void exitLog(LogsParser.LogContext ctx) {
        this.entries.add(this.current);
    }

    @Override public void exitEntry(LogsParser.EntryContext ctx) {
        this.entries.add(this.current);
    }


    public List<LogEntry> getEntries() {
        return this.entries;
    }

}
