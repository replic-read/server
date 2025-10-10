package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.NotUniqueException;
import com.rere.server.domain.model.exception.NotUniqueSubject;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.service.AccountService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation of the {@link AccountService} backed by an {@link AccountRepository}.
 */
@Component
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public List<Account> getAccounts(Comparator<Account> sort, UUID accountId, Set<AccountState> stateFilter, String query) {
        Stream<Account> accStream = accountRepo.getAll()
                .stream()
                .filter(account -> accountId == null || account.getId().equals(accountId))
                .filter(account -> stateFilter == null || stateFilter.contains(account.getAccountState()))
                .filter(account -> query == null || account.getEmail().contains(query) || account.getUsername().contains(query));

        if (sort != null) {
            accStream = accStream.sorted(sort);
        }

        return accStream.toList();
    }

    @Nonnull
    @Override
    public Optional<Account> getAccountById(@Nonnull UUID accountId) {
        return getAccounts(null, accountId, null, null)
                .stream()
                .findFirst();
    }

    @Nonnull
    @Override
    public Account updateAccount(UUID accountId, String email, String username, int profileColor) throws NotUniqueException, NotFoundException {
        Account account = getAccountById(accountId)
                .orElseThrow(() -> NotFoundException.account(accountId));

        if (!account.getEmail().equals(email)) {
            if (getByEmail(email).isPresent()) {
                throw new NotUniqueException(NotUniqueSubject.EMAIL);
            }
            account.setEmail(email);
            account.setAccountState(AccountState.UNVERIFIED);
        }
        if (!account.getUsername().equals(username)) {
            if (getByUsername(username).isPresent()) {
                throw new NotUniqueException(NotUniqueSubject.USERNAME);
            }
            account.setUsername(username);
        }
        account.setProfileColor(profileColor);

        return accountRepo.saveModel(account);
    }

    @Override
    public Optional<Account> getByEmail(String email) {
        return accountRepo.getAll()
                .stream()
                .filter(acc -> acc.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<Account> getByUsername(String username) {
        return accountRepo.getAll()
                .stream()
                .filter(acc -> acc.getUsername().equals(username))
                .findFirst();
    }
}
