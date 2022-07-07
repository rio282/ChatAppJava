package nl.hva.caj.utils;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {

    // logger settings
    private static final String INFO = "INFO";
    private static final String ERROR = "ERROR";

    private static final String INFO_SHORTHAND = "?";
    private static final String ERROR_SHORTHAND = "!";

    public static boolean shorthand = false;

    // timer vars
    private static long start = Long.MAX_VALUE;
    private static long end = Long.MAX_VALUE;

    public static void useShorthand(boolean shorthand) {
        Logger.shorthand = shorthand;
    }

    public static void log(@Nullable Object message) {
        final String TIMESTAMP = now();
        final String LEVEL = (shorthand) ? INFO_SHORTHAND : INFO;

        assert message != null;
        System.out.printf("%s [%s] - %s\n", TIMESTAMP, LEVEL, message.toString());
    }

    public static void logf(String message, Object... args) {
        log(String.format(message, args));
    }


    public static void err(Object message) {
        final String TIMESTAMP = now();
        final String LEVEL = (shorthand) ? ERROR_SHORTHAND : ERROR;

        System.err.printf("%s [%s] - %s\n", TIMESTAMP, LEVEL, message.toString());
    }

    public static void errf(String message, Object... args) {
        err(String.format(message, args));
    }


    private static String now() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd | hh:mm:ss");
        return dtf.format(LocalDateTime.now());
    }

    public static void startTimer() {
        start = System.nanoTime();
    }

    public static void endTimer() {
        end = System.nanoTime();
    }

    /**
     * @return The time between <code>startTimer()</code> and <code>endTimer()</code> in milliseconds. <br>
     * Returns a negative number if <code>startTimer()</code> or <code>endTimer()</code> haven't been called yet.
     */
    public static long getTimerResultMillis() {
        return getTimerResultNanos() / 1000000;
    }

    /**
     * @return The time between <code>startTimer()</code> and <code>endTimer()</code> in nanoseconds. <br>
     * Returns a negative number if <code>startTimer()</code> or endTimer()</code> haven't been called yet.
     */
    public static long getTimerResultNanos() {
        return (start == Long.MAX_VALUE || end == Long.MAX_VALUE) ? -1 : (end - start);
    }
}
