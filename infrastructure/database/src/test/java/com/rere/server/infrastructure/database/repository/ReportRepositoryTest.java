package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.account.Account;
import com.rere.server.domain.model.impl.ReportImpl;
import com.rere.server.domain.model.replic.Replic;
import com.rere.server.domain.model.report.Report;

import java.util.UUID;


class ReportRepositoryTest extends BaseRepositoryTest<Report> {

    @Override
    protected Report create(UUID id) {
        return ReportImpl.builder()
                .id(id)
                .replicId(id)
                .build();
    }

    @Override
    protected void insertDependencies(Report model) {
        UUID accountId = model.getAuthorId() != null ? model.getAuthorId() : UUID.randomUUID();
        Account account = createUniqueAccount(accountId);
        Replic replic = createUniqueReplic(model.getReplicId());
        replicRepo.saveModel(replic);
        accountRepo.saveModel(account);
    }

}
