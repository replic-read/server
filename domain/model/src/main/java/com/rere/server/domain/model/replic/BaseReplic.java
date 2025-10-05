package com.rere.server.domain.model.replic;

import com.rere.server.domain.model.account.Account;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

import java.net.URL;
import java.time.Instant;
import java.util.UUID;

/**
 * Models a base replic, i.e. a replic without associated file data.
 */
@Value
@NonFinal
public class BaseReplic {

    /**
     * The id of the replic.
     */
    @NonNull
    UUID id;

    /**
     * The creation timestamp of the replic.
     */
    @NonNull
    Instant creationTimestamp;

    /**
     * The url of the resource that was replicated-
     */
    @NonNull
    URL originalUrl;

    /**
     * The media mode of te replic.
     */
    @NonNull
    MediaMode mediaMode;

    /**
     * The description of the replic, if it exists.
     */
    String description;

    /**
     * The timestamp of expiration of this replic, if it has one.
     */
    Instant expirationTimestamp;

    /**
     * The hashed password of the replic, if it exists.
     */
    String passwordHash;

    /**
     * The account owner of the replic, if it exists.
     */
    Account owner;

    /**
     * Checks whether a password is required to access this replic.
     * @return True if a password is required.
     */
    public boolean requiresPassword() {
        return getPasswordHash() != null;
    }

    /**
     * Checks whether a replic is expired at this specific time.
     * @return True if the replic is expired.
     */
    public boolean isExpired() {
        return getExpirationTimestamp() != null && getExpirationTimestamp().isBefore(Instant.now());
    }

}
