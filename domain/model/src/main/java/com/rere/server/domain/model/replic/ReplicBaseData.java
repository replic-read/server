package com.rere.server.domain.model.replic;

import lombok.NonNull;

import java.net.URL;
import java.time.Instant;
import java.util.UUID;

/**
 * Models the base data of a replic.
 */
public interface ReplicBaseData {

    /**
     * The id of the replic.
     */
    @NonNull
    UUID getId();

    /**
     * The creation timestamp of the replic.
     */
    @NonNull
    Instant getCreationTimestamp();

    /**
     * The url of the resource that was replicated-
     */
    @NonNull
    URL getOriginalUrl();

    /**
     * The media mode of te replic.
     */
    @NonNull
    MediaMode getMediaMode();

    /**
     * The current state of the replic.
     */
    @NonNull
    ReplicState getState();

    /**
     * Sets the state of the replic.
     * @param state The new state.
     */
    void setState(@NonNull ReplicState state);

    /**
     * The description of the replic, if it exists.
     */
    String getDescription();

    /**
     * The timestamp of expiration of this replic, if it has one.
     */
    Instant getExpirationTimestamp();

    /**
     * The hashed password of the replic, if it exists.
     */
    String getPasswordHash();

    /**
     * The account owner of the replic, if it exists.
     */
    UUID getOwnerId();

    /**
     * Checks whether a password is required to access this replic.
     * @return True if a password is required.
     */
    default boolean requiresPassword() {
        return getPasswordHash() != null;
    }

    /**
     * Checks whether a replic is expired at this specific time.
     * @param now The current reference timestamp.
     * @return True if the replic is expired.
     */
    default boolean isExpired(Instant now) {
        return getExpirationTimestamp() != null && getExpirationTimestamp().isBefore(now);
    }

}
