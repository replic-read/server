package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.AuthTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link AuthTokenEntity} table.
 */
@Component
public interface AuthTokenCrudRepository extends CrudRepository<AuthTokenEntity, UUID> {
}
