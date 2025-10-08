package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ReportEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * The report crud interface.
 */
@Repository
public interface ReportCrudRepository extends CrudRepository<ReportEntity, UUID> {
}
