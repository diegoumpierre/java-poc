package com.poc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The proposal for the class it is show the basic structure for a test class.
 *
 * @author diegoUmpierre
 * @since Oct 27 2023
 */
class LogLevelTest {

    private String start = "[";
    private String end = "]: ";


    @Test
    void task1GetLogMessageSuccess(){
        String messageExpected = "Invalid operation";
        String message = LogLevels.message("[ERROR]: Invalid operation");
        assertEquals(messageExpected, message);
    }

    @Test
    void task1GetLogMessageCleanTheStringShouldSuccess(){
        String messageExpected = "Disk almost full";
        String message = LogLevels.message("[WARNING]:  Disk almost full\r\n");
        assertEquals(messageExpected, message);
    }

    @Test
    void task2GetLoglevelLogLineShouldSuccess(){
        String messageExpected = "error";
        String message = LogLevels.logLevel("[ERROR]: Invalid operation");
        assertEquals(messageExpected, message);
    }

    @Test
    void task3ReformatAlogLineShouldSuccess(){
        String messageExpected = "Operation completed (info)";
        String message = LogLevels.reformat("[INFO]: Operation completed");
        assertEquals(messageExpected, message);
    }
}