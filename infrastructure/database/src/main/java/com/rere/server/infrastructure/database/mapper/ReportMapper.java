package com.rere.server.infrastructure.database.mapper;

import com.rere.server.domain.model.report.Report;
import com.rere.server.infrastructure.database.repository.jpa.AccountCrudRepository;
import com.rere.server.infrastructure.database.repository.jpa.ReplicCrudRepository;
import com.rere.server.infrastructure.database.table.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ReportMapper implements EntityMapper<ReportEntity, Report> {

    private final AccountCrudRepository accountRepo;
    private final ReplicCrudRepository replicRepo;

    @Autowired
    ReportMapper(AccountCrudRepository accountRepo, ReplicCrudRepository replicRepo) {
        this.accountRepo = accountRepo;
        this.replicRepo = replicRepo;
    }

    @Override
    public ReportEntity map(Report model) {
        ReportEntity entity = new ReportEntity(
                replicRepo.findById(model.getReplicId()).orElseThrow(),
                model.getAuthorId() != null ?
                        accountRepo.findById(model.getAuthorId())
                                .orElseThrow() : null,
                model.getDescription(),
                model.getState()
        );
        entity.setId(model.getId());
        entity.setCreationTimestamp(model.getCreationTimestamp());
        return entity;
    }
}
