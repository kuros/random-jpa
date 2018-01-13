package com.github.kuros.random.jpa.exception;

public class MethodInvocationException extends RuntimeException {

    public MethodInvocationException(final String message) {
        super(message);
    }

    public MethodInvocationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
