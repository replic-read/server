package com.rere.server.domain.model.account;

import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Models an authentication token.
 */
@Data
public class AuthToken {

    /**
     * The id of the token.
     */
    @NonNull
    private final UUID id;

    /**
     * The timestamp of creation of the token.
     */
    @NonNull
    private final Instant creationTimestamp;

    /**
     * The actual token that is exposed.
     */
    @NonNull
    private final UUID token;

    /**
     * The expiration of the token.
     */
    @NonNull
    private final Instant expirationTimestamp;

    /**
     * The account the token is referenced by.
     */
    @NonNull
    private final Account account;

    /**
     * The type of token.
     */
    @NonNull
    private final AuthTokenType type;

    /**
     * The optional data that is stored along the token.
     */
    private final String data;

    /**
     * Whether the token has been actively invalidated.
     */
    private boolean invalidated;

    /**
     * Checks whether the token is valid for a given set of expectations, as well regarding expiration and invalidation.
     * @param targetAccount The account that the token is expected to be for.
     * @param expectedType  The expected token type.
     * @return True if the token is valid.
     */
    public boolean isValid(Account targetAccount, AuthToken expectedType) {
        boolean expired = getExpirationTimestamp().isBefore(Instant.now());
        boolean notInvalidated = !isInvalidated();
        boolean validAccount = account.getId().equals(targetAccount.getId());
        boolean typeMatches = type.equals(expectedType.getType());

        return expired && notInvalidated && validAccount && typeMatches;
    }

}
