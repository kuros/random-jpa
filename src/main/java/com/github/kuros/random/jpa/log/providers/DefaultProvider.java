package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.util.Util;

import java.util.logging.Level;

class DefaultProvider implements Logger {

    private java.util.logging.Logger logger;

    private DefaultProvider(final Class<?> clazz) {
        this.logger = java.util.logging.Logger.getLogger(clazz.getName());
    }

    @VisibleForTesting
    DefaultProvider(final java.util.logging.Logger logger) {
        this.logger = logger;
    }

    public void info(final String message, final Object... args) {
        logger.log(Level.INFO, Util.formatMessage(message, args));
    }

    public void info(final String message, final Throwable throwable) {
        logger.log(Level.INFO, message, throwable);
    }

    public void debug(final String message, final Object... args) {
        logger.log(Level.FINEST, Util.formatMessage(message, args));
    }

    public void debug(final String message, final Throwable throwable) {
        logger.log(Level.FINEST, message, throwable);
    }

    public void warn(final String message, final Object... args) {
        logger.log(Level.WARNING, Util.formatMessage(message, args));
    }

    public void warn(final String message, final Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);
    }

    public void error(final String message, final Object... args) {
        logger.log(Level.SEVERE, message, args);
    }

    public void error(final String message, final Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
}
