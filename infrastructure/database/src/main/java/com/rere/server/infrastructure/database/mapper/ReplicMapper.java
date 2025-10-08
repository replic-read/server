package com.rere.server.infrastructure.database.mapper;

import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.infrastructure.database.repository.jpa.AccountCrudRepository;
import com.rere.server.infrastructure.database.table.ReplicEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReplicMapper implements EntityMapper<ReplicEntity, ReplicBaseData> {

    private final AccountCrudRepository accountRepo;

    @Autowired
    ReplicMapper(AccountCrudRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @Override
    public ReplicEntity map(ReplicBaseData model) {
        ReplicEntity entity = new ReplicEntity(
                model.getOriginalUrl().toString(),
                model.getMediaMode(),
                model.getState(),
                model.getDescription(),
                model.getExpirationTimestamp(),
                model.getPasswordHash(),
                accountRepo.findById(model.getOwnerId()).orElseThrow()
        );
        entity.setId(model.getId());
        entity.setCreationTimestamp(model.getCreationTimestamp());
        return entity;
    }
}
