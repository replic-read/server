package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.domain.model.account.AuthToken;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.repository.BaseJpaRepositoryAdapter;
import com.rere.server.infrastructure.database.table.AuthTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * {@link CrudRepository} for the {@link AuthTokenEntity} table.
 */
@Component
public abstract class AuthTokenCrudRepository extends BaseJpaRepositoryAdapter<AuthToken, AuthTokenEntity> {
    @Autowired
    protected AuthTokenCrudRepository(EntityMapper<AuthTokenEntity, AuthToken> mapper) {
        super(mapper);
    }
}
