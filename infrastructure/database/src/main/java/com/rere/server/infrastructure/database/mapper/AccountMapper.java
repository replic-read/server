package com.rere.server.infrastructure.database.mapper;

import com.rere.server.domain.model.account.Account;
import com.rere.server.infrastructure.database.table.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper implements EntityMapper<AccountEntity, Account> {

    @Override
    public AccountEntity map(Account model) {
        AccountEntity entity = new AccountEntity(
                model.getEmail(),
                model.getUsername(),
                model.getPasswordHash(),
                model.isAdmin(),
                model.getProfileColor(),
                model.getAccountState()
        );
        entity.setId(model.getId());
        entity.setCreationTimestamp(model.getCreationTimestamp());
        return entity;
    }
}
