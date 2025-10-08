package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.AuthTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The auth-token crud interface.
 */
@Repository
public interface AuthTokenCrudRepository extends CrudRepository<AuthTokenEntity, UUID> {
}
