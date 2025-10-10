package com.rere.server.domain.service;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.exception.InvalidTokenException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.OperationDisabledException;
import jakarta.annotation.Nonnull;

import java.util.Optional;
import java.util.UUID;

/**
 * Service that provides methods for authentication.
 */
public interface AuthenticationService {

    /**
     * Authenticates an account based on credentials.
     * <br>
     * Either email or username need to be passed.
     * If both are passed, both need to match.
     * @param email The email of the account.
     * @param username The username of the account.
     * @param password The unencrypted password of the account.
     * @return An account if the credentials were valid, or an empty optional.
     */
    @Nonnull
    Optional<Account> authenticateWithCredentials(String email, String username, @Nonnull String password);

    /**
     * Authenticates an account based on a jwt.
     * @param jwt The jwt.
     * @return An account if the jwt was valid, or an empty optional.
     */
    @Nonnull
    Optional<Account> authenticateWithJwt(@Nonnull String jwt);

    /**
     * Authenticates an account with a refresh token.
     * @param refreshToken The refresh token.
     * @return An account if the refresh token was valid, or an empty optional.
     */
    @Nonnull
    Optional<Account> authenticateWithRefreshToken(@Nonnull UUID refreshToken);

    /**
     * Creates an access token (jwt) for a specific user.
     * @param accountId The id of the account.
     * @return A new access token.
     * @throws NotFoundException If the account was not found.
     */
    @Nonnull
    String createAccessToken(@Nonnull UUID accountId) throws NotFoundException;

    /**
     * Creates a refresh token for a specific user.
     * @param accountId The id of the account.
     * @return A new refresh token.
     * @throws NotFoundException If the account was not found.
     */
    @Nonnull
    UUID createRefreshToken(@Nonnull UUID accountId) throws NotFoundException;

    /**
     * Creates a new account.
     * @param email The email.
     * @param username The username.
     * @param password The password.
     * @param profileColor The profile color.
     * @param isAdmin Whether the account will be an admin account.
     * @param sendEmail Whether the verification-email should immediately be sent.
     * @param bypassConfig Whether the account should be created even if the config forbids it.
     * @return The new account.
     * @throws NotUniqueException If either email or username were not unique.
     * @throws OperationDisabledException If creating accounts is disabled.
     */
    @Nonnull
    Account createAccount(@Nonnull String email, @Nonnull String username, @Nonnull String password,
                          int profileColor, boolean isAdmin, boolean isVerified, boolean sendEmail,
                          boolean bypassConfig) throws NotUniqueException, OperationDisabledException;

    /**
     * Ensures that only one admin account exists, with username, email and password as specified via the config.
     * <br>
     * If it can, turns an existing account into admin first.
     * If that is not possible, a new admin-account is created.
     * @return The new admin account.
     * @throws NotUniqueException In the case that an account exists with either admin email or username, but doesn't match the other value.
     */
    @Nonnull
    Account ensureSingletonAdmin() throws NotUniqueException;

    /**
     * Requests a verification message to be sent to the account.
     * @param accountId The id of the account.
     * @throws NotFoundException If the account was not found.
     */
    void requestEmailVerification(@Nonnull UUID accountId) throws NotFoundException;

    /**
     * Validates an email based off an email-verification-token.
     * @param authToken The auth token-
     * @return The account whose email was verified.
     * @throws InvalidTokenException If the token was not a valid email-verification-token.
     */
    @Nonnull
    Account validateEmail(@Nonnull UUID authToken) throws InvalidTokenException;

}
