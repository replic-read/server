package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ReplicEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ReplicEntity} table.
 */
@Component
public interface ReplicCrudRepository extends CrudRepository<ReplicEntity, UUID> {
}
