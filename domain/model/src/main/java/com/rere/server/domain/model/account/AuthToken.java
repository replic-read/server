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

}
