package com.rere.server.domain.model.account;

import java.time.Instant;
import java.util.UUID;

/**
 * Models an account of a user.
 */
public interface Account {

    /**
     * The id of the account.
     */
    UUID getId();

    /**
     * The timestamp when the account was created.
     */
    Instant getCreationTimestamp();

    /**
     * The email of the account.
     */
    String getEmail();

    void setEmail(String email);

    /**
     * The username of the account.
     */
    String getUsername();

    void setUsername(String username);

    /**
     * The hashed password of the account.
     */
    String getPasswordHash();

    void setPasswordHash(String passwordHash);

    /**
     * Whether the account is an admin account.
     */
    boolean isAdmin();

    void setAdmin(boolean admin);

    /**
     * The current state of the account.
     */
    AccountState getAccountState();

    void setAccountState(AccountState accountState);

    /**
     * The profile color of the account.
     */
    int getProfileColor();

    void setProfileColor(int profileColor);

}
