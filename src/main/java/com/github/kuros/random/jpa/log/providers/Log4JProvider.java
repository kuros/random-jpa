package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.util.Util;

class Log4JProvider implements Logger {

    private final org.apache.log4j.Logger logger;

    private Log4JProvider(final Class<?> clazz) {
        this.logger = org.apache.log4j.Logger.getLogger(clazz);

    }

    @VisibleForTesting
    Log4JProvider(final org.apache.log4j.Logger logger) {
        this.logger = logger;
    }

    public void info(final String message, final Object... args) {
        final String formatMessage = Util.formatMessage(message, args);
        logger.info(formatMessage);
    }

    public void info(final String message, final Throwable throwable) {
        logger.info(message, throwable);
    }


    public void debug(final String message, final Object... args) {
        final String formatMessage = Util.formatMessage(message, args);
        logger.debug(formatMessage);
    }

    public void debug(final String message, final Throwable throwable) {
        logger.info(message, throwable);
    }

    public void warn(final String message, final Object... args) {
        final String formatMessage = Util.formatMessage(message, args);
        logger.warn(formatMessage);
    }

    public void warn(final String message, final Throwable throwable) {
        logger.warn(message, throwable);
    }

    public void error(final String message, final Object... args) {
        final String formatMessage = Util.formatMessage(message, args);
        logger.error(formatMessage);
    }

    public void error(final String message, final Throwable throwable) {
        logger.error(message, throwable);
    }


}
