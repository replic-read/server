package com.rere.server.infrastructure.database.mapper;

import com.rere.server.domain.model.report.Report;
import com.rere.server.infrastructure.database.repository.jpa.JpaRepositories;
import com.rere.server.infrastructure.database.table.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class ReportMapper implements EntityMapper<ReportEntity, Report> {

    private final JpaRepositories.Account accountRepo;
    private final JpaRepositories.Replic replicRepo;

    @Autowired
    ReportMapper(JpaRepositories.Account accountRepo, JpaRepositories.Replic replicRepo) {
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
