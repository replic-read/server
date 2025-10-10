package com.rere.server.domain.service;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import jakarta.annotation.Nonnull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service that provides access to the accounts.
 */
public interface AccountService {

    /**
     * Gets all account matching the filters.
     * @param sort The comparator that defines how to sort the accounts.
     * @param accountId The account id to search for.
     * @param stateFilter Filters which states to filter for.
     * @param query The query for searching accounts.
     * @return All accounts matching the filters.
     */
    @Nonnull
    List<Account> getAccounts(Comparator<Account> sort, UUID accountId, Set<AccountState> stateFilter, String query);

    /**
     * Gets the account by its id.
     * @param accountId The id of the account.
     * @return The account, or an empty optional.
     */
    @Nonnull
    Optional<Account> getAccountById(@Nonnull UUID accountId);

    /**
     * Gets a user by email.
     * @param email The email.
     * @return The account, or empty.
     */
    Optional<Account> getByEmail(String email);

    /**
     * Gets a user by username.
     * @param username The username.
     * @return The account, or empty.
     */
    Optional<Account> getByUsername(String username);

    /**
     * Updates selected values of the account.
     * @param accountId The id of the account to update.
     * @param email The new email of the account.
     * @param username The new username of the account.
     * @param profileColor The new profile color of the account.
     * @return The updated account, or empty if the account was not found.
     * @throws NotUniqueException If email or username were not unique.
     * @throws NotFoundException If the account was not found.
     */
    @Nonnull
    Account updateAccount(@Nonnull UUID accountId, @Nonnull String email, @Nonnull String username, int profileColor) throws NotUniqueException, NotFoundException;

}
