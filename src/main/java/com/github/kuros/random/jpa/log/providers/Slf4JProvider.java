package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.annotation.VisibleForTesting;
import com.github.kuros.random.jpa.log.Logger;
import org.slf4j.LoggerFactory;

class Slf4JProvider implements Logger {

    private org.slf4j.Logger logger;

    Slf4JProvider(final Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    @VisibleForTesting
    Slf4JProvider(final org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public void info(final String message, final Object... args) {
        logger.info(message, args);
    }

    public void info(final String message, final Throwable throwable) {
        logger.info(message, throwable);
    }

    public void debug(final String message, final Object... args) {
        logger.debug(message, args);
    }

    public void debug(final String message, final Throwable throwable) {
        logger.debug(message, throwable);
    }

    public void warn(final String message, final Object... args) {
        logger.warn(message, args);
    }

    public void warn(final String message, final Throwable throwable) {
        logger.warn(message, throwable);
    }

    public void error(final String message, final Object... args) {
        logger.error(message, args);
    }

    public void error(final String message, final Throwable throwable) {
        logger.error(message, throwable);
    }
}
