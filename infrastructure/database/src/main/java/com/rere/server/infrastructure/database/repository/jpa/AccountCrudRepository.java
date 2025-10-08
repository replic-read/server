package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The account crud interface.
 */
@Repository
public interface AccountCrudRepository extends CrudRepository<AccountEntity, UUID> {
}
