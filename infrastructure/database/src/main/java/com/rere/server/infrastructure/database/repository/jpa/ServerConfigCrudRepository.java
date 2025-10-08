package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.repository.BaseJpaRepositoryAdapter;
import com.rere.server.infrastructure.database.table.ServerConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * {@link CrudRepository} for the {@link ServerConfigEntity} table.
 */
@Component
public abstract class ServerConfigCrudRepository extends BaseJpaRepositoryAdapter<ServerConfig, ServerConfigEntity> {
    @Autowired
    protected ServerConfigCrudRepository(EntityMapper<ServerConfigEntity, ServerConfig> mapper) {
        super(mapper);
    }
}
