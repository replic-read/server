package com.rere.server.domain.model.account;

import java.time.Instant;
import java.util.UUID;

/**
 * Models an authentication token.
 */
public interface AuthToken {

    /**
     * The id of the token.
     */
    UUID getId();

    /**
     * The timestamp of creation of the token.
     */
    Instant getCreationTimestamp();

    /**
     * The actual token that is exposed.
     */
    UUID getToken();

    /**
     * The expiration of the token.
     */
    Instant getExpirationTimestamp();

    /**
     * The account the token is referenced by.
     */
    UUID getAccountId();

    /**
     * The type of token.
     */
    AuthTokenType getType();

    /**
     * The optional data that is stored along the token.
     */
    String getData();

    /**
     * Whether the token has been actively invalidated.
     */
    boolean isInvalidated();

    /**
     * Sets the invalidated state.
     * @param invalidated The new invalidated state.
     */
    void setInvalidated(boolean invalidated);

    /**
     * Checks whether the token is valid for a given set of expectations, as well regarding expiration and invalidation.
     * @param now The current timestamp as reference
     * @param expectedType  The expected token type.
     * @return True if the token is valid.
     */
    default boolean isValid(AuthTokenType expectedType, Instant now) {
        boolean expired = getExpirationTimestamp().isBefore(now);
        boolean notInvalidated = !isInvalidated();
        boolean typeMatches = getType().equals(expectedType);

        return !expired && notInvalidated && typeMatches;
    }

}
