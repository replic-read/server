package com.rere.server.domain.model.exception;

import com.rere.server.domain.model.account.AuthToken;

/**
 * Thrown if an invalid {@link AuthToken} was presented.
 */
public class InvalidTokenException extends DomainException {

    private static final String MESSAGE = "A provided auth token was invalid.";

    /**
     * Creates a new InvalidTokenExpiration.
     */
    public InvalidTokenException() {
        super(MESSAGE);
    }
}
