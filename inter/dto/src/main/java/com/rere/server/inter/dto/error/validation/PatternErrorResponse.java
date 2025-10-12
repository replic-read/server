package com.rere.server.inter.dto.error.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Models an error info that occurred because a field did not match a specific pattern.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class PatternErrorResponse extends ValidationErrorResponse {

    /**
     * The required regex pattern.
     */
    private final String pattern;

    /**
     * Creates a new PatternErrorResponse.
     * @param pattern The expected pattern.
     * @param value The value that did not match the pattern.
     */
    public PatternErrorResponse(String pattern, Object value) {
        super(ValidationErrorType.PATTERN, value);
        this.pattern = pattern;
    }
}
