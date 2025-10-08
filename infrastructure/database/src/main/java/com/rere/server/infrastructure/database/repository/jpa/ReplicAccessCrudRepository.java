package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.domain.model.replic.ReplicAccess;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.repository.BaseJpaRepositoryAdapter;
import com.rere.server.infrastructure.database.table.ReplicAccessEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * {@link CrudRepository} for the {@link ReplicAccessEntity} table.
 */
@Component
public abstract class ReplicAccessCrudRepository extends BaseJpaRepositoryAdapter<ReplicAccess, ReplicAccessEntity> {
    @Autowired
    protected ReplicAccessCrudRepository(EntityMapper<ReplicAccessEntity, ReplicAccess> mapper) {
        super(mapper);
    }
}
