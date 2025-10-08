package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.infrastructure.database.table.ReportEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ReportEntity} table.
 */
@Component
public interface ReportCrudRepository extends CrudRepository<ReportEntity, UUID> {
}
