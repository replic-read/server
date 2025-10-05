package com.rere.server.domain.model.exception;

import lombok.Getter;

/**
 * Possible operations that can cause an {@link OperationDisabledException} to be thrown.
 */
@Getter
public enum OperationDisabledOperation {

    /**
     * If signing up is disabled.
     */
    SIGNUP("signup"),

    /**
     * If reporting a replic is disabled for a specific author.
     */
    REPORT("report a replic");

    private final String name;

    /**
     * Creates a new OperationDisabledOperation.
     * @param name The name of the operationn
     */
    OperationDisabledOperation(String name) {
        this.name = name;
    }
}
