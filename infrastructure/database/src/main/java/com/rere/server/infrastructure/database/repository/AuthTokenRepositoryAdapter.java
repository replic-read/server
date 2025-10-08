package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.table.AuthTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link AuthTokenEntity} table.
 */
@Component
public class AuthTokenRepositoryAdapter extends BaseJpaRepositoryAdapter<AuthToken, AuthTokenEntity> {
    @Autowired
    protected AuthTokenRepositoryAdapter(CrudRepository<AuthTokenEntity, UUID> delegate, EntityMapper<AuthTokenEntity, AuthToken> mapper) {
        super(delegate, mapper);
    }
}
