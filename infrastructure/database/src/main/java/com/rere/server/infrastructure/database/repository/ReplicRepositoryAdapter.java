package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.table.ReplicEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ReplicEntity} table.
 */
@Component
public class ReplicRepositoryAdapter extends BaseJpaRepositoryAdapter<ReplicBaseData, ReplicEntity> {
    @Autowired
    protected ReplicRepositoryAdapter(CrudRepository<ReplicEntity, UUID> delegate, EntityMapper<ReplicEntity, ReplicBaseData> mapper) {
        super(delegate, mapper);
    }
}
