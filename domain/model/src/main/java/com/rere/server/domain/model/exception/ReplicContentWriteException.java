package com.rere.server.domain.model.exception;

/**
 * Thrown when writing the content of the replic to a file was unsuccessful.
 */
public class ReplicContentWriteException extends DomainException {

    private static final String MESSAGE = "An error occurred when trying to write the replic content to file.";

    /**
     * Creates a new ReplicContentWriteException.
     */
    public ReplicContentWriteException() {
        super(MESSAGE);
    }

}
