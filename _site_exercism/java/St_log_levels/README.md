## Log Levels
Introduction
Strings
A String in Java is an object that represents immutable text as a sequence of Unicode characters (letters, digits, punctuation, etc.). Double quotes are used to define a String instance:

String fruit = "Apple";
Strings are manipulated by calling the string's methods. Once a string has been constructed, its value can never change. Any methods that appear to modify a string will actually return a new string. The String class provides some static methods to transform the strings.

Instructions
In this exercise you'll be processing log-lines.

Each log line is a string formatted as follows: "[<LEVEL>]: <MESSAGE>".

There are three different log levels:

INFO
WARNING
ERROR
You have three tasks, each of which will take a log line and ask you to do something with it.

### Task1: Get message from a log line
Implement the (static) LogLevels.message() method to return a log line's message:

LogLevels.message("[ERROR]: Invalid operation")
// => "Invalid operation"

### Task2: Get Log level from a log line
Implement the (static) LogLevels.logLevel() method to return a log line's log level, which should be returned in lowercase:

LogLevels.logLevel("[ERROR]: Invalid operation")
// => "

### Task3: Reformat a log line
Implement the (static) LogLevels.reformat() method that reformats the log line, putting the message first and the log level after it in parentheses:

LogLevels.reformat("[INFO]: Operation completed")
// => "Operation completed (info)"