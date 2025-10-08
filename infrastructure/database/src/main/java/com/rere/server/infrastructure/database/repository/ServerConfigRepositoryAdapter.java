package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.domain.repository.ServerConfigRepository;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.table.ServerConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ServerConfigEntity} table.
 */
@Component
public class ServerConfigRepositoryAdapter extends BaseJpaRepositoryAdapter<ServerConfig, ServerConfigEntity>
        implements ServerConfigRepository {
    @Autowired
    protected ServerConfigRepositoryAdapter(CrudRepository<ServerConfigEntity, UUID> delegate, EntityMapper<ServerConfigEntity, ServerConfig> mapper) {
        super(delegate, mapper);
    }

    @Override
    public Optional<ServerConfig> getConfig() {
        return getAll().stream().findFirst();
    }

    @Override
    public void saveConfig(ServerConfig serverConfig) {
        clear();
        saveModel(serverConfig);
    }
}
