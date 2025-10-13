package com.rere.server.domain.model.account;

/**
 * Different types of auth tokens.
 */
public enum AuthTokenType {

    /**
     * Token is used to perform an email verification on an account.
     */
    EMAIL_VERIFICATION,

    /**
     * Token is used as a refresh token.
     */
    REFRESH_TOKEN,

    /**
     * Token is used to reset a password.
     */
    PASSWORD_RESET

}
