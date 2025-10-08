package com.rere.server.infrastructure.database.repository.jpa;

import com.rere.server.domain.model.report.Report;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.repository.BaseJpaRepositoryAdapter;
import com.rere.server.infrastructure.database.table.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * {@link CrudRepository} for the {@link ReportEntity} table.
 */
@Component
public abstract class ReportCrudRepository extends BaseJpaRepositoryAdapter<Report, ReportEntity> {
    @Autowired
    protected ReportCrudRepository(EntityMapper<ReportEntity, Report> mapper) {
        super(mapper);
    }
}
