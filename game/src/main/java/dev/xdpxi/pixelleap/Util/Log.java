package dev.xdpxi.pixelleap.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

public class Log {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final BlockingQueue<LogEntry> logQueue = new LinkedBlockingQueue<>(10000);
    private static final LogLevel currentLogLevel = LogLevel.INFO;
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "LoggerThread");
        t.setDaemon(true);
        return t;
    });

    static {
        logExecutor.submit(Log::processLogQueue);
        Runtime.getRuntime().addShutdownHook(new Thread(Log::shutdown));
    }

    private static void log(LogLevel level, String message, Object... args) {
        if (level.ordinal() >= currentLogLevel.ordinal()) {
            LogEntry entry = new LogEntry(level, message, args);
            if (!logQueue.offer(entry)) {
                System.err.println("Log queue full, discarding log entry: " + entry);
            }
        }
    }

    private static void processLogQueue() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LogEntry entry = logQueue.poll(100, TimeUnit.MILLISECONDS);
                if (entry != null) {
                    String formattedDateTime = LocalDateTime.now().format(FORMATTER);
                    String formattedMessage = entry.args.length > 0 ? String.format(entry.message, entry.args) : entry.message;
                    System.out.printf("[%s] [%s] : %s%n", formattedDateTime, entry.level, formattedMessage);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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

    private static void shutdown() {
        logExecutor.shutdownNow();
        try {
            if (!logExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                System.err.println("Logger thread did not terminate in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private record LogEntry(LogLevel level, String message, Object[] args) {
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}