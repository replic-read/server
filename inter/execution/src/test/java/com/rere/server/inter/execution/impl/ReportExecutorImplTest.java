package com.rere.server.inter.execution.impl;

import com.rere.server.domain.model.exception.DomainException;
import com.rere.server.domain.model.exception.NotFoundException;
import com.rere.server.domain.model.impl.AccountImpl;
import com.rere.server.domain.model.impl.ReportImpl;
import com.rere.server.domain.model.report.Report;
import com.rere.server.domain.model.report.ReportState;
import com.rere.server.inter.authorization.AuthorizationException;
import com.rere.server.inter.dto.parameter.ReportSortParameter;
import com.rere.server.inter.dto.request.CreateReportRequest;
import com.rere.server.inter.dto.response.ReportResponse;
import com.rere.server.inter.execution.HttpErrorResponseException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportExecutorImplTest extends BaseExecutorTest {

    @InjectMocks
    private ReportExecutorImpl subject;

    @Test
    void getReportsPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.getReports(null, null, null, null));
    }

    @Test
    void getReportsDelegatesToServiceAndFiltersId() {
        List<Report> reports = IntStream.range(0, 10)
                .mapToObj(i -> (Report) ReportImpl.builder().build())
                .toList();
        when(reportService.getReports(any(), any()))
                .thenReturn(reports);

        UUID id = reports.get(7).getId();

        List<ReportResponse> response = subject.getReports(ReportSortParameter.DATE, null, id, null);

        assertEquals(1, response.size());
        assertEquals(id.toString(), response.getFirst().id());
    }

    @Test
    void createReportPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.createReport(new CreateReportRequest(""), UUID.randomUUID()));
    }

    @Test
    void createReportCallsService() throws DomainException {
        when(reportService.reportReplic(any(), any(), any()))
                .thenReturn(ReportImpl.builder().build());

        subject.createReport(new CreateReportRequest(""), UUID.randomUUID());

        verify(reportService).reportReplic(any(), any(), any());
    }

    @Test
    void createReportConvertsDomainException() throws DomainException {
        when(reportService.reportReplic(any(), any(), any()))
                .thenThrow(NotFoundException.replic(UUID.randomUUID()));
        subject.setAuth(AccountImpl.builder().build());

        assertThrows(HttpErrorResponseException.class,
                () -> subject.createReport(new CreateReportRequest(""), UUID.randomUUID()));
    }

    @Test
    void updateReportPropagatesAuthorization() {
        assertAuthorizationIsPropagated(() -> subject.updateReport(UUID.randomUUID(), ReportState.CLOSED));
    }

    @Test
    void updateReportCallsService() throws DomainException {
        when(reportService.updateReportState(any(), any()))
                .thenReturn(ReportImpl.builder().build());

        subject.updateReport(UUID.randomUUID(), ReportState.CLOSED);

        verify(reportService).updateReportState(any(), any());
    }

    @Test
    void updateReportConvertsDomainException() throws DomainException {
        when(reportService.updateReportState(any(), any()))
                .thenThrow(NotFoundException.replic(UUID.randomUUID()));

        assertThrows(AuthorizationException.class,
                () -> subject.updateReport(UUID.randomUUID(), ReportState.CLOSED));
    }


}