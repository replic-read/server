package com.rere.server.domain.service.impl;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.account.AccountState;
import com.rere.server.domain.repository.AccountRepository;
import com.rere.server.domain.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation of the {@link AccountService} backed by an {@link AccountRepository}.
 */
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
}
