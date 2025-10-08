package com.rere.server.domain.model.replic;

import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Models an access to a replic.
 *
 */
public interface ReplicAccess {

    /**
     * The id of the replic-access.
     */
    @NonNull
    UUID getId();

    /**
     * The timestamp of creation.
     */
    @NonNull
    Instant getCreationTimestamp();

    /**
     * The replic.
     */
    @NonNull
    UUID getReplicId();

    /**
     * The visitor, or null.
     */
    UUID getVisitorId();

}
