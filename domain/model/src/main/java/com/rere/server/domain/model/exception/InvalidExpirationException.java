package com.rere.server.domain.model.exception;

import lombok.Getter;

import java.time.Instant;

/**
 * Thrown when the provided expiration did not match the requirements by the config.
 */
@Getter
public class InvalidExpirationException extends DomainException {

    private static final String MESSAGE_MISSING = "No expiration was provided, despite being required.";
    private static final String MESSAGE_OVER_LIMIT_MESSAGE = "The provided expiration of %s is after the limit of %s.";

    /**
     * Whether the problem was that no expiration was provided.
     */
    private final boolean expirationMissing;

    /**
     * The maximum possible expiration.
     */
    private final Instant maximumExpiration;

    /**
     * The expiration that was provided, and was longer than the maximum one.
     */
    private final Instant providedExpiration;

    /**
     * Creates a new InvalidExpirationException.
     * @param expirationMissing Whether no expiration was provided.
     * @param maximumExpiration The maximum expiration.
     * @param providedExpiration The provided expiration.
     */
    public InvalidExpirationException(boolean expirationMissing, Instant maximumExpiration, Instant providedExpiration) {
        super(expirationMissing ? MESSAGE_MISSING : MESSAGE_OVER_LIMIT_MESSAGE.formatted(providedExpiration, maximumExpiration));
        this.expirationMissing = expirationMissing;
        this.maximumExpiration = maximumExpiration;
        this.providedExpiration = providedExpiration;
    }
}
