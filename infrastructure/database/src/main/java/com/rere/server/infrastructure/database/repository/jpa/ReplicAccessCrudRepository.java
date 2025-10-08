package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ReplicAccessEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The replic-access crud interface.
 */
@Repository
public interface ReplicAccessCrudRepository extends CrudRepository<ReplicAccessEntity, UUID> {
}
