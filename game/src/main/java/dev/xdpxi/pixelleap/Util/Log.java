package dev.xdpxi.pixelleap.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static void log(String level, String message, Object... args) {
        String formattedDateTime = LocalDateTime.now().format(FORMATTER).replace(" ", "] [");
        String formattedMessage = args.length > 0 ? String.format(message, args) : message;
        System.out.printf("[%s] [%s] %s%n", formattedDateTime, level.toUpperCase(), formattedMessage);
    }

    public static void debug(String message, Object... args) {
        log("DEBUG", message, args);
    }

    public static void info(String message, Object... args) {
        log("INFO", message, args);
    }

    public static void warn(String message, Object... args) {
        log("WARN", message, args);
    }

    public static void error(String message, Object... args) {
        log("ERROR", message, args);
    }
}