package com.github.kuros.random.jpa.log.providers;

import com.github.kuros.random.jpa.log.Logger;
import org.slf4j.LoggerFactory;

public class Slf4JProvider implements Logger {

    private org.slf4j.Logger logger;

    public Slf4JProvider(final Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
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

    public void error(final String message, final Object... args) {
        logger.error(message, args);
    }

    public void error(final String message, final Throwable throwable) {
        logger.error(message, throwable);
    }
}
