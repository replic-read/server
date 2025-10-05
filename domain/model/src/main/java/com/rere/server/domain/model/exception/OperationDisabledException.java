package com.rere.server.domain.model.exception;

import lombok.Getter;

/**
 * Thrown when a specific operation is disabled.
 */
@Getter
public class OperationDisabledException extends DomainException {

    private static final String MESSAGE_FORMAT = "The operation '%s' is disabled.";

    /**
     * The operation that is disabled.
     */
    private final OperationDisabledOperation operation;

    /**
     * Creates a new OperationDisabledException.
     *
     * @param operation The disabled operation.
     */
    public OperationDisabledException(OperationDisabledOperation operation) {
        super(MESSAGE_FORMAT.formatted(operation));
        this.operation = operation;
    }
}
