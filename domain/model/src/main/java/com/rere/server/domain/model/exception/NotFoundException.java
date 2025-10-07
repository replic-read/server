package com.rere.server.domain.model.exception;

import lombok.Getter;

/**
 * Exception that indicates that something was not found.
 */
@Getter
public class NotFoundException extends DomainException {

    private static final String MESSAGE_FORMAT = "Did not find a '%s' with identifier '%s'.";

    /**
     * The kind of object that was not found.
     */
    private final NotFoundSubject subject;

    /**
     * The identifier the object was searched by.
     */
    private final transient Object identifier;

    /**
     * Creates a new NotFoundException.
     * @param subject The subject.
     * @param identifier The identifier.
     */
    public NotFoundException(NotFoundSubject subject, Object identifier) {
        super(MESSAGE_FORMAT.formatted(subject.getName(), identifier.toString()));
        this.subject = subject;
        this.identifier = identifier;
    }
}
