package com.rere.server.infrastructure.database.mapper;

import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.infrastructure.database.repository.jpa.AccountCrudRepository;
import com.rere.server.infrastructure.database.table.AuthTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenMapper implements EntityMapper<AuthTokenEntity, AuthToken> {

    private final AccountCrudRepository accountRepository;

    @Autowired
    AuthTokenMapper(AccountCrudRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public AuthTokenEntity map(AuthToken model) {
        AuthTokenEntity entity = new AuthTokenEntity(
                model.getExpirationTimestamp(),
                model.getToken(),
                accountRepository.findById(model.getAccountId())
                        .orElseThrow(),
                model.isInvalidated(),
                model.getType(),
                model.getData()
        );
        entity.setId(model.getId());
        entity.setCreationTimestamp(model.getCreationTimestamp());
        return entity;
    }
}
