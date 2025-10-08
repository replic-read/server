package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ServerConfigEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ServerConfigEntity} table.
 */
@Component
public interface ServerConfigCrudRepository extends CrudRepository<ServerConfigEntity, UUID> {
}
