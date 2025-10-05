package com.rere.server.domain.model.exception;

/**
 * Thrown when a password for a replic was invalid.
 */
public class InvalidPasswordException extends DomainException {

    private static final String MESSAGE = "The provided password is incorrect.";

    /**
     * Creates a new DomainException with a given message.
     */
    public InvalidPasswordException() {
        super(MESSAGE);
    }
}
