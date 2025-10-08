package com.rere.server.infrastructure.database.repository;

import com.rere.server.domain.model.report.Report;
import com.rere.server.infrastructure.database.mapper.EntityMapper;
import com.rere.server.infrastructure.database.table.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * {@link CrudRepository} for the {@link ReportEntity} table.
 */
@Component
public abstract class ReportRepositoryAdapter extends BaseJpaRepositoryAdapter<Report, ReportEntity> {
    @Autowired
    protected ReportRepositoryAdapter(CrudRepository<ReportEntity, UUID> delegate, EntityMapper<ReportEntity, Report> mapper) {
        super(delegate, mapper);
    }
}
