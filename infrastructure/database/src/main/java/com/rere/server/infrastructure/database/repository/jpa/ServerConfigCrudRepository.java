package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ServerConfigEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The server-config crud interface.
 */
@Repository
public interface ServerConfigCrudRepository extends CrudRepository<ServerConfigEntity, UUID> {
}
