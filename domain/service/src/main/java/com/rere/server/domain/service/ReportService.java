package com.rere.server.domain.service;

import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.exception.OperationDisabledException;
import com.rere.server.domain.model.report.Report;
import jakarta.annotation.Nonnull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service that provides access to the reports.
 */
public interface ReportService {

    /**
     * Gets al reports.
     * @param sort How to sort the reports.
     * @param query The query filter.
     * @return All reports matching the above filter.
     */
    @Nonnull
    List<Report> getReports(Comparator<Report> sort, String query);

    /**
     * Reports a specific replic.
     * @param replicId The id of the replic to report.
     * @param accountId The id of the account that reports the replic.
     * @param description The description of the report.
     * @return The created report.
     * @throws NotFoundException If the replic or account wasn't found.
     * @throws OperationDisabledException If the operation is not allowed.
     */
    @Nonnull
    Report reportReplic(@Nonnull UUID replicId, UUID accountId, String description) throws
            NotFoundException, OperationDisabledException;

}
