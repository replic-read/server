package com.rere.server.domain.model.exception;

import lombok.Getter;

/**
 * Subjects whose "not finding" can cause a {@link NotFoundException}.
 */
@Getter
public enum NotFoundSubject {

    /**
     * A replic was not found.
     */
    REPLIC("replic"),

    /**
     * An account was not found.
     */
    ACCOUNT("account"),

    /**
     * A file of a replic was not found.
     */
    REPLIC_FILE("replic file"),

    /**
     * A report was not found.
     */
    REPORT("report");

    private final String name;

    /**
     * Creates a new subject.
     * @param name The name of the subject.
     */
    NotFoundSubject(String name) {
        this.name = name;
    }
}
