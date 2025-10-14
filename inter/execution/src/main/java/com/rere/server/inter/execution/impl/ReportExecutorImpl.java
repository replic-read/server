package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.model.report.ReportState;
import com.rere.server.domain.service.AccountService;
import com.rere.server.domain.service.AuthenticationService;
import com.rere.server.domain.service.QuotaService;
import com.rere.server.domain.service.ReplicService;
import com.rere.server.domain.service.ReportService;
import com.rere.server.domain.service.ServerConfigService;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.authorization.Authorizer;
import com.rere.server.inter.dto.error.HttpErrorResponseException;
import com.rere.server.inter.dto.parameter.ReportSortParameter;
import com.rere.server.inter.dto.parameter.SortDirectionParameter;
import com.rere.server.inter.dto.request.CreateReportRequest;
import com.rere.server.inter.dto.response.ReportResponse;
import com.rere.server.inter.execution.AbstractExecutor;
import com.rere.server.inter.execution.ReportExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Primary
@Component
public class ReportExecutorImpl extends AbstractExecutor implements ReportExecutor<ReportState, ReportSortParameter, SortDirectionParameter, UUID> {
    @Autowired
    protected ReportExecutorImpl(AccountService accountService, AuthenticationService authService, ReplicService replicService, ReportService reportService, ServerConfigService configService, QuotaService quotaService, Authorizer authorizer) {
        super(accountService, authService, replicService, reportService, configService, quotaService, authorizer);
    }

    @Override
    public List<ReportResponse> getReports(ReportSortParameter sort, SortDirectionParameter direction, UUID reportId, String query) {
        authorizer.requireAccessReports(getAuth());
        return reportService
                .getReports(getComparatorFromSort(sort, direction), query)
                .stream().filter(report -> reportId == null || report.getId().equals(reportId))
                .map(this::createReportResponse)
                .toList();
    }

    @Override
    public ReportResponse createReport(CreateReportRequest request, UUID replicId) {
        authorizer.requireCreateReports(getAuth());

        UUID userId = getAuth() != null ? getAuth().getId() : null;
        try {
            Report report = reportService.reportReplic(replicId, userId, request.description());
            return createReportResponse(report);
        } catch (DomainException e) {
            throw HttpErrorResponseException.fromDomainException(e);
        }
    }

    @Override
    public ReportResponse updateReport(UUID reportId, ReportState state) {
        authorizer.requireReviewReports(getAuth());

        try {
            Report report = reportService.updateReportState(reportId, state);
            return createReportResponse(report);
        } catch (NotFoundException e) {
            throw AuthorizationException.genericForbidden();
        }
    }
}
