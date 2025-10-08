package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.impl.ReplicAccessImpl;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.replic.ReplicAccess;

import java.util.UUID;


class ReplicAccessRepositoryTest extends BaseRepositoryTest<ReplicAccess> {

    @Override
    protected ReplicAccess create(UUID id) {
        return ReplicAccessImpl.builder()
                .id(id)
                .replicId(id)
                .build();
    }

    @Override
    protected void insertDependencies(ReplicAccess model) {
        UUID accountId = model.getVisitorId() != null ? model.getVisitorId() : UUID.randomUUID();
        Account account = createUniqueAccount(accountId);
        Replic replic = createUniqueReplic(model.getReplicId());
        replicRepo.saveModel(replic);
        accountRepo.saveModel(account);
    }

}
