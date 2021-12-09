package com.vicious.viciouslib.core;

public abstract class CommunicationBridge {

    public Boolean debugMode;

    public CommunicationBridge(boolean debugMode) {
        this.debugMode = debugMode;
    }

    // Logs general information to console
    public abstract void info(String message);

    // Logs a warning to console
    public abstract void warn(String message);

    // Logs an error to console
    public abstract void error(String message);

    // Sends a message to developers and logs to the console when debug mode is enabled
    public abstract void debug(String message);

    // Sends the stack trace to the console
    public abstract void reportStackTrace(String application, Exception exception, boolean directToDiscord);

    // Overload assuming not sending to discord
    public void reportStackTrace(String application, Exception exception) {
        reportStackTrace(application, exception, false);
    }

    // Reports the error
    public abstract void reportError(String message, boolean discordDirectly);

    // Overload assuming not sending to discord
    public void reportError(String message) {
        reportError(message, false);
    }
}
