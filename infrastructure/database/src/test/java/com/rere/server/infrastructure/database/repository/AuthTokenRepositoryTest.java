package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.domain.model.impl.AuthTokenImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;


@Slf4j
class AuthTokenRepositoryTest extends BaseRepositoryTest<AuthToken> {

    @Override
    protected AuthToken create(UUID id) {
        return AuthTokenImpl.builder()
                .id(id)
                .token(id)
                .accountId(UUID.randomUUID())
                .build();
    }

    @Override
    protected void insertDependencies(AuthToken model) {
        accountRepo.saveModel(createUniqueAccount(model.getAccountId()));
    }

}
