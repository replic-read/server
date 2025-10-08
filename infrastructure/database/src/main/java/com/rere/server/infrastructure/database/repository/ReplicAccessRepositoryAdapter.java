package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.replic.ReplicAccess;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.table.ReplicAccessEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ReplicAccessEntity} table.
 */
@Component
public class ReplicAccessRepositoryAdapter extends BaseJpaRepositoryAdapter<ReplicAccess, ReplicAccessEntity> {
    @Autowired
    protected ReplicAccessRepositoryAdapter(CrudRepository<ReplicAccessEntity, UUID> delegate, EntityMapper<ReplicAccessEntity, ReplicAccess> mapper) {
        super(delegate, mapper);
    }
}
