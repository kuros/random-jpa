package com.github.kuros.random.jpa.exception;

/**
 * Created by kumarro on 13/12/16.
 */
public class ResultNotFoundException extends RandomJPAException {
    public ResultNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ResultNotFoundException(final String message) {
        super(message);
    }

    public ResultNotFoundException(final Throwable cause) {
        super(cause);
    }
}
