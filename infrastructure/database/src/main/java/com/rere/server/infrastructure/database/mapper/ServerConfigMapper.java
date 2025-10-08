package com.rere.server.infrastructure.database.mapper;

import com.rere.server.domain.model.config.ServerConfig;
import com.rere.server.infrastructure.database.table.ServerConfigEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ServerConfigMapper implements EntityMapper<ServerConfigEntity, ServerConfig> {

    @Override
    public ServerConfigEntity map(ServerConfig model) {
        ServerConfigEntity entity = new ServerConfigEntity(
                model.getCreateReplicsGroup(),
                model.getAccessReplicsGroup(),
                model.getCreateReportsGroup(),
                model.isAllowAccountCreation(),
                model.getLimit() != null ? model.getLimit().getPeriod() : null,
                model.getLimit() != null ? model.getLimit().getPeriodStart() : null,
                model.getLimit() != null ? model.getLimit().getCount() : null,
                model.getMaximumActivePeriod()
        );
        entity.setId(UUID.randomUUID());
        entity.setCreationTimestamp(Instant.now());
        return entity;
    }
}
