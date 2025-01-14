package dev.xdpxi.pixelleap.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Log {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private static final LogLevel currentLogLevel = LogLevel.INFO;

    static {
        Thread loggerThread = new Thread(Log::processLogQueue);
        loggerThread.setDaemon(true);
        loggerThread.start();
    }

    private static void log(LogLevel level, String message, Object... args) {
        if (level.ordinal() >= currentLogLevel.ordinal()) {
            String formattedDateTime = LocalDateTime.now().format(FORMATTER);
            String formattedMessage = args.length > 0 ? String.format(message, args) : message;
            String logEntry = String.format("[%s] [%s] %s", formattedDateTime, level, formattedMessage);
            if (!logQueue.offer(logEntry)) {
                System.err.println("Failed to add log entry to queue: " + logEntry);
            }
        }
    }

    private static void processLogQueue() {
        while (true) {
            try {
                String logEntry = logQueue.poll(100, TimeUnit.MILLISECONDS);
                if (logEntry != null) {
                    System.out.println(logEntry);
                }
            } catch (InterruptedException e) {
                System.err.println("Error in log processing: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public static void debug(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }

    public static void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    public static void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    public static void error(String message, Object... args) {
        log(LogLevel.ERROR, message, args);
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}