package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link AccountEntity} table.
 */
@Component
public interface AccountCrudRepository extends CrudRepository<AccountEntity, UUID> {
}
