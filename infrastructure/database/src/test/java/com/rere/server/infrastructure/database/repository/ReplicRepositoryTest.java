package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.replic.ReplicBaseData;

import java.util.UUID;


class ReplicRepositoryTest extends BaseRepositoryTest<ReplicBaseData> {

    @Override
    protected ReplicBaseData create(UUID id) {
        return createUniqueReplic(id);
    }

    @Override
    protected void insertDependencies(ReplicBaseData model) {
        UUID accountId = model.getOwnerId() != null ? model.getOwnerId() : UUID.randomUUID();
        Account account = createUniqueAccount(accountId);
        accountRepo.saveModel(account);
    }

}
