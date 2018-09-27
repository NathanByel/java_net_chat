package com.java_net_chat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Log {
    public static final String ANSI_RESET   = "\u001B[0m";
    public static final String ANSI_BLACK   = "\u001B[30m";
    public static final String ANSI_RED     = "\u001B[31m";
    public static final String ANSI_GREEN   = "\u001B[32m";
    public static final String ANSI_YELLOW  = "\u001B[33m";
    public static final String ANSI_BLUE    = "\u001B[34m";
    public static final String ANSI_PURPLE  = "\u001B[35m";
    public static final String ANSI_CYAN    = "\u001B[36m";
    public static final String ANSI_WHITE   = "\u001B[37m";

    private static void colorPrint(String color, String className, String message) {
        System.out.printf("%s[%s] %s : %s%s\r\n", color,
                LocalTime.now().format( DateTimeFormatter.ofPattern("HH:mm:ss") ),
                className, message, ANSI_RESET);
    }

    //info
    public static void i(String className, String message) {
        colorPrint(ANSI_GREEN, className, message);
    }

    //error
    public static void e(String className, String message) {
        colorPrint(ANSI_RED, className, message);
    }

    //debug
    public static void d(String className, String message) {
        colorPrint(ANSI_BLUE, className, message);
    }

    //warning
    public static void w(String className, String message) {
        colorPrint(ANSI_YELLOW, className, message);
    }
}
