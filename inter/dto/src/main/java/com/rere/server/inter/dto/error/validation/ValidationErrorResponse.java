package com.rere.server.inter.dto.error.validation;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.ErrorType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Error info about an error that was caused by a failed validation of a field.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public abstract class ValidationErrorResponse extends ErrorResponseInfo {

    /**
     * The validation error type.
     */
    private final ValidationErrorType validationType;

    /**
     * The value that did not pass validation.
     */
    private final Serializable value;

    /**
     * Creates a new ValidationErrorResponse.
     * @param validationType The validation error type.
     * @param value The value that did not pass validation.
     */
    protected ValidationErrorResponse(ValidationErrorType validationType, Serializable value) {
        super(ErrorType.VALIDATION);
        this.validationType = validationType;
        this.value = value;
    }
}