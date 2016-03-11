package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.log.Logger;
import com.github.kuros.random.jpa.util.Util;

import java.text.MessageFormat;

public class Log4JProvider implements Logger {

    private org.apache.log4j.Logger logger;
    private MessageFormat messageFormat;

    private Log4JProvider(final Class<?> clazz) {
        this.logger = org.apache.log4j.Logger.getLogger(clazz);

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

    public void error(final String message, final Object... args) {
        final String formatMessage = Util.formatMessage(message, args);
        logger.error(formatMessage);
    }

    public void error(final String message, final Throwable throwable) {
        logger.error(message, throwable);
    }


}
