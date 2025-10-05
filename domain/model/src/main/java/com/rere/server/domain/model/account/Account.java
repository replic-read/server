package com.rere.server.domain.model.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Models an account of a user.
 */
@Data
@AllArgsConstructor
public class Account {

    /**
     * The id of the account.
     */
    @NonNull
    private final UUID id;

    /**
     * The timestamp when the account was created.
     */
    @NonNull
    private final Instant creationTimestamp;

    /**
     * The email of the account.
     */
    @NonNull
    private String email;

    /**
     * The username of the account.
     */
    @NonNull
    private String username;

    /**
     * The hashed password of the account.
     */
    @NonNull
    private String passwordHash;

    /**
     * Whether the account is an admin account.
     */
    private boolean isAdmin;

    /**
     * The current state of the account.
     */
    @NonNull
    private AccountState accountState;

    /**
     * The profile color of the account.
     */
    private int profileColor;

}
