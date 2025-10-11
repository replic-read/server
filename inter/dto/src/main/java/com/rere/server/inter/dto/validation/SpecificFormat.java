package com.rere.server.inter.dto.validation;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Period;
import java.time.format.DateTimeParseException;

/**
 * Specific formats that can't be validated with a regex.
 */
public enum SpecificFormat {

    /**
     * A url with query parameters.
     */
    URL,

    /**
     * A {@link Period} parsable string.
     */
    JAVA_PERIOD,

    /**
     * A positive integer.
     */
    POSITIVE_INTEGER;

    private static boolean validateJavaPeriod(Object value) {
        try {
            Period.parse(value.toString());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean validateUrl(Object value) {
        try {
            URI.create(value.toString()).toURL();
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * ValidChecks whether the value is valid according to the validation type.
     * @param value The value.
     * @return Whether the value is valid.
     */
    public boolean isValid(Object value) {
        return switch (this) {
            case URL -> validateUrl(value);
            case JAVA_PERIOD -> validateJavaPeriod(value);
            case POSITIVE_INTEGER -> validatePositiveInteger(value);
        };
    }

    private boolean validatePositiveInteger(Object value) {
        try {
            Integer.valueOf(value.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
