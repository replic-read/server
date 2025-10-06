package com.rere.server.domain.model.exception;

import lombok.Getter;

/**
 * Subjects that can cause a {@link NotUniqueException}.
 */
@Getter
public enum NotUniqueSubject {

    /**
     * An email was not unique.
     */
    EMAIL("email"),

    /**
     * A username was not unique.
     */
    USERNAME("username");

    /**
     * The name of the subject.
     */
    private final String name;

    /**
     * Creates a new NotUniqueSubject.
     * @param name The name.
     */
    NotUniqueSubject(String name) {
        this.name = name;
    }
}
