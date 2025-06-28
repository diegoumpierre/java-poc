package com.poc;

/**
 * The proposal for the class it is show the basic structure.
 *
 * @author diegoUmpierre
 * @since Sep 26 2023
 */
public class LogLevels {

    private static final String start = "[";
    private static final String end = "]: ";

    public static String message(String logLine) {
        String messageResult = logLine.substring(logLine.indexOf(end)+3,logLine.length());
        messageResult = messageResult.replaceAll("\\r\\n|\\r|\\n", "").trim();
        return messageResult;
    }

    public static String logLevel(String logLine) {
        String messageResult = logLine.substring(logLine.indexOf(start)+1,logLine.indexOf(end));
        return messageResult.toLowerCase();
    }

    public static String reformat(String logLine) {
        return message(logLine)+ " ("+logLevel(logLine)+")";
    }
}
