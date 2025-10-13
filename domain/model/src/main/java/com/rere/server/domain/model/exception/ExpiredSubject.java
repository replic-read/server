package com.rere.server.domain.model.exception;

import lombok.Getter;

/**
 * Subjects that can cause an {@link ExpiredException}.
 */
@Getter
public enum ExpiredSubject {

    /**
     * A replic was expired.
     */
    REPLIC("replic");

    /**
     * The name of the subject.
     */
    private final String name;

    ExpiredSubject(String name) {
        this.name = name;
    }
}
