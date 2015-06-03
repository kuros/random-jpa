package com.github.kuros.random.jpa.exception;

/**
 * Created by Kumar Rohit on 5/31/15.
 */
public class RandomJPAException extends RuntimeException {

    public RandomJPAException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RandomJPAException(final String message) {
        super(message);
    }
}
