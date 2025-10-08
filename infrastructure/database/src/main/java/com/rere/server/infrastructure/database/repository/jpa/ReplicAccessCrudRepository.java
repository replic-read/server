package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ReplicAccessEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ReplicAccessEntity} table.
 */
@Component
public interface ReplicAccessCrudRepository extends CrudRepository<ReplicAccessEntity, UUID> {
}
