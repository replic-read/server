package com.rere.server.domain.model.report;

import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Models a report.
 *
 * @param <R> The replic type.
 */
public interface Report {

    /**
     * The id of the report.
     */
    @NonNull
    UUID getId();

    /**
     * The creation timestamp of the report.
     */
    @NonNull
    Instant getCreationTimestamp();

    /**
     * The report for which the report was created.
     */
    @NonNull
    UUID getReplicId();

    /**
     * The description of the report.
     */
    String getDescription();

    /**
     * The account the report was created by, if it exists.
     */
    UUID getAuthorId();

    /**
     * The state the report currently is in.
     */
    @NonNull
    ReportState getState();

    void setState(@NonNull ReportState state);

}
