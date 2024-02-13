package logger;

import config.Config;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public class Logger {
    public static synchronized void logError(String message, Throwable throwable) {
        try (FileWriter fileWriter = new FileWriter(Config.LOG_FILE, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            printWriter.println(message + " - " + throwable.toString());
            throwable.printStackTrace(printWriter);
            printWriter.println();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }
}
