package io.gitlab.arturbosch.javaparser.support;

import java.io.PrintStream;

/**
 * @author Artur Bosch
 */
public final class Out {

    private static PrintStream printer = System.out;

    private Out() {
    }

    public static void println(String message) {
        printer.println(message);
    }

    public static void setPrinter(PrintStream stream) {
        Out.printer = stream;
    }
}
