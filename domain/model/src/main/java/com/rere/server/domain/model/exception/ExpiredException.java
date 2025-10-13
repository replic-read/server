package com.rere.server.domain.model.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Exception indicating that something is expired.
 */
@Getter
public class ExpiredException extends DomainException {

    private static final String MESSAGE_FORMAT = "The %s with id '%s' is expired.";

    private final UUID id;
    private final ExpiredSubject subject;

    private ExpiredException(UUID id, ExpiredSubject subject) {
        super(MESSAGE_FORMAT.formatted(subject.name(), id.toString()));
        this.id = id;
        this.subject = subject;
    }

    /**
     * Creates a new ExpiredException for a replic.
     *
     * @param replicId The id of the replic.
     * @return The exception.
     */
    public static ExpiredException replic(UUID replicId) {
        return new ExpiredException(replicId, ExpiredSubject.REPLIC);
    }
}
