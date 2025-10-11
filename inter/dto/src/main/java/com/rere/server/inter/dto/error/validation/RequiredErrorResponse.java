package com.rere.server.inter.dto.error.validation;


/**
 * Models an error info that occurred because a required field was not present.
 */
public class RequiredErrorResponse extends ValidationErrorResponse {

    public RequiredErrorResponse() {
        super(ValidationErrorType.REQUIRED, null);
    }
}
