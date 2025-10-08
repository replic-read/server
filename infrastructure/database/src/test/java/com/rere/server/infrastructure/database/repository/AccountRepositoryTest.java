package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.Account;

import java.util.UUID;


class AccountRepositoryTest extends BaseRepositoryTest<Account> {

    @Override
    protected Account create(UUID id) {
        return createUniqueAccount(id);
    }

    @Override
    protected void insertDependencies(Account model) {
        // Account doesnt have any foreign keys.
    }

}
