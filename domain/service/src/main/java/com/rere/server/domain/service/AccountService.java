package com.rere.server.domain.service;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
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

}
