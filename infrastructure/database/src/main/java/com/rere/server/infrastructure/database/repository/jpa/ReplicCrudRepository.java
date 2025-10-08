package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ReplicEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The replic crud interface.
 */
@Repository
public interface ReplicCrudRepository extends CrudRepository<ReplicEntity, UUID> {
}
