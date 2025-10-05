package com.rere.server.domain.model.exception;

/**
 * Exception class that is thrown by the domain layer.
 */
public class DomainException extends Exception {

    /**
     * Creates a new DomainException with a given message.
     * @param message The message.
     */
    public DomainException(String message) {
        super(message);
    }
}
