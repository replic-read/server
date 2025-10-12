package com.rere.server.inter.dto.error.validation;


import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Models an error info that occurred because a required field was not present.
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class RequiredErrorResponse extends ValidationErrorResponse {

    public RequiredErrorResponse() {
        super(ValidationErrorType.REQUIRED, null);
    }
}
