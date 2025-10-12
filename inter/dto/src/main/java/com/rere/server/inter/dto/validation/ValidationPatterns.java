package com.rere.server.inter.dto.validation;

/**
 * Contains regex patterns used for validation.
 */
public final class ValidationPatterns {

    /**
     * Regex for a username.
     */
    public static final String USERNAME = "^[A-Za-z0-9_]{4,32}$";

    /**
     * Regex for an email.
     */
    public static final String EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    /**
     * Regex for a password.
     */
    public static final String PASSWORD = "^[^\\s]{4,32}$";

    /**
     * Regex for a uuid.
     */
    public static final String UUID = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

    /**
     * Regex for an iso-8601 instant.
     */
    public static final String INSTANT = "^(?:[1-9]\\d{3}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1\\d|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[1-9]\\d(?:0[48]|[2468][048]|[13579][26])|(?:[2468][048]|[13579][26])00)-02-29)T(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d(?:Z|[+-][01]\\d:[0-5]\\d)$";


    private ValidationPatterns() {
    }

}
