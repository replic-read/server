package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.domain.model.replic.ReplicBaseData;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.repository.BaseJpaRepositoryAdapter;
import com.rere.server.infrastructure.database.table.ReplicEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * {@link CrudRepository} for the {@link ReplicEntity} table.
 */
@Component
public abstract class ReplicCrudRepository extends BaseJpaRepositoryAdapter<ReplicBaseData, ReplicEntity> {
    @Autowired
    protected ReplicCrudRepository(EntityMapper<ReplicEntity, ReplicBaseData> mapper) {
        super(mapper);
    }
}
