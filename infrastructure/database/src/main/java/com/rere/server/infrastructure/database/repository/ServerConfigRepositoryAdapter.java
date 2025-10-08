package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.table.ServerConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ServerConfigEntity} table.
 */
@Component
public abstract class ServerConfigRepositoryAdapter extends BaseJpaRepositoryAdapter<ServerConfig, ServerConfigEntity> {
    @Autowired
    protected ServerConfigRepositoryAdapter(CrudRepository<ServerConfigEntity, UUID> delegate, EntityMapper<ServerConfigEntity, ServerConfig> mapper) {
        super(delegate, mapper);
    }
}
