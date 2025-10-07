package com.rere.server.domain.model.exception;

import lombok.Getter;

/**
 * Thrown when a value that should've been unique was not unique.
 */
@Getter
public class NotUniqueException extends DomainException {

    private static final String MESSAGE_FORMAT = "A value of type '%s' was not unique.";

    private final NotUniqueSubject subject;

    /**
     * Creates a new NotUniqueException.
     * @param subject The subject that wasn't unique.
     */
    public NotUniqueException(NotUniqueSubject subject) {
        super(MESSAGE_FORMAT.formatted(subject.getName()));
        this.subject = subject;
    }
}
