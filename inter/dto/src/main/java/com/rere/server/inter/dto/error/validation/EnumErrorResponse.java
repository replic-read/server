package com.rere.server.inter.dto.error.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Models an error info that occurred because a field did not match a specific pattern.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class EnumErrorResponse extends ValidationErrorResponse {

    /**
     * The values that were allowed.
     */
    private final String[] allowedValues;

    /**
     * Creates a new EnumErrorResponse.
     * @param allowedValues The allowed values.
     * @param value The actual value.
     */
    public EnumErrorResponse(String[] allowedValues, Object value) {
        super(ValidationErrorType.ENUM, value);
        this.allowedValues = allowedValues.clone();
    }
}
