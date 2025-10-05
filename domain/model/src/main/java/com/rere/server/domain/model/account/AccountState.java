package com.rere.server.domain.model.account;

/**
 * Models states an account can be in.
 */
public enum AccountState {

    /**
     * The account is active and usable.
     */
    ACTIVE,

    /**
     * The account was deactivated.
     */
    INACTIVE,

    /**
     * The account, i.e. its email, has not yet been verified.
     */
    UNVERIFIED

}
