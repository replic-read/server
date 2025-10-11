package com.rere.server.inter.dto.error.validation;

/**
 * Different validation error types.
 */
public enum ValidationErrorType {

    /**
     * A field could not be validated via a regex.
     */
    PATTERN,

    /**
     * A field was out of bounds.
     */
    BOUNDS,

    /**
     * A field was required but not present.
     */
    REQUIRED,

    /**
     * A fields value was not in an enum.
     */
    ENUM

}
