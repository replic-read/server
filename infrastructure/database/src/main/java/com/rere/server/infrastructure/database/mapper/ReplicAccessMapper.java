package com.rere.server.infrastructure.database.mapper;

import com.rere.server.domain.model.replic.ReplicAccess;
import com.rere.server.infrastructure.database.repository.jpa.JpaRepositories;
import com.rere.server.infrastructure.database.table.ReplicAccessEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReplicAccessMapper implements EntityMapper<ReplicAccessEntity, ReplicAccess> {

    private final JpaRepositories.Replic replicRepo;
    private final JpaRepositories.Account accountRepo;

    @Autowired
    ReplicAccessMapper(JpaRepositories.Replic replicRepo, JpaRepositories.Account accountRepo) {
        this.replicRepo = replicRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    public ReplicAccessEntity map(ReplicAccess model) {
        ReplicAccessEntity entity = new ReplicAccessEntity(
                model.getVisitorId() != null ?
                        accountRepo.findById(model.getVisitorId())
                                .orElseThrow() : null,
                replicRepo.findById(model.getReplicId())
                        .orElseThrow()
        );
        entity.setId(entity.getId());
        entity.setCreationTimestamp(entity.getCreationTimestamp());
        return entity;
    }
}
