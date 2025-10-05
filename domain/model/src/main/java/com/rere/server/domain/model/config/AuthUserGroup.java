package com.rere.server.domain.model.config;

/**
 * Models specific groups of users based on their auth state.
 */
public enum AuthUserGroup {

    /**
     * Groups that contains every user.
     */
    ALL,

    /**
     * Group that contains only users that have an account, i.e. are not anonymous.
     */
    ACCOUNT,

    /**
     * Group that contains only users whose account is verified.
     */
    VERIFIED

}
