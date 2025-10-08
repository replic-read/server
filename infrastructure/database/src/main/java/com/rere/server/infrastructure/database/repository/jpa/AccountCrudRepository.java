package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.domain.model.account.Account;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.repository.BaseJpaRepositoryAdapter;
import com.rere.server.infrastructure.database.table.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * {@link CrudRepository} for the {@link AccountEntity} table.
 */
@Component
public abstract class AccountCrudRepository extends BaseJpaRepositoryAdapter<Account, AccountEntity> {
    @Autowired
    protected AccountCrudRepository(EntityMapper<AccountEntity, Account> mapper) {
        super(mapper);
    }
}
