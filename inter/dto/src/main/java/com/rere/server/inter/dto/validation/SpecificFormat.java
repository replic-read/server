package com.rere.server.inter.dto.validation;

import java.time.Period;

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
    POSITIVE_INTEGER

}
