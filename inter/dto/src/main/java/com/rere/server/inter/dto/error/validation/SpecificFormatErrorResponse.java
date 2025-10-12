package com.rere.server.inter.dto.error.validation;

import com.rere.server.inter.dto.validation.SpecificFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Locale;

/**
 * Models an error info that occurred because a field did not match a specific format.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public final class SpecificFormatErrorResponse extends ValidationErrorResponse {

    /**
     * The expected specific format.
     */
    private final String format;

    /**
     * Creates a new SpecificFormatErrorResponse.
     * @param value The value.
     * @param format The specific format.
     */
    public SpecificFormatErrorResponse(Serializable value, SpecificFormat format) {
        super(ValidationErrorType.PATTERN, value);
        this.format = format.name().toLowerCase(Locale.ROOT);
    }
}
