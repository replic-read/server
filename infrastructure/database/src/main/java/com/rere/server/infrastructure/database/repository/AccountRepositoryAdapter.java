package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.Account;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.table.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link AccountEntity} table.
 */
@Component
public class AccountRepositoryAdapter extends BaseJpaRepositoryAdapter<Account, AccountEntity> {
    @Autowired
    protected AccountRepositoryAdapter(CrudRepository<AccountEntity, UUID> delegate, EntityMapper<AccountEntity, Account> mapper) {
        super(delegate, mapper);
    }
}
